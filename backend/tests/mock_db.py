import copy
import re

class MockCursor:
    def __init__(self, docs):
        self._docs = docs
        self._limit = None

    def sort(self, key, direction=1):
        # direction 1 = ascending, -1 = descending
        if isinstance(key, str):
            reverse = (direction == -1)
            self._docs.sort(key=lambda d: d.get(key, 0) or 0, reverse=reverse)
        return self

    def limit(self, count):
        self._limit = count
        return self

    async def to_list(self, length=100):
        docs = self._docs[:length] if self._limit is None else self._docs[:min(self._limit, length)]
        return [copy.deepcopy(d) for d in docs]

class MockUpdateResult:
    def __init__(self, matched_count=0, modified_count=0):
        self.matched_count = matched_count
        self.modified_count = modified_count

class MockCollection:
    def __init__(self):
        self.docs = []

    def _matches(self, doc, query):
        if not query:
            return True
        for k, v in query.items():
            if k == "$or":
                if not any(self._matches(doc, cond) for cond in v):
                    return False
                continue
            if isinstance(v, dict):
                val = doc.get(k)
                if "$ne" in v and val == v["$ne"]:
                    return False
                if "$in" in v and val not in v["$in"]:
                    return False
                if "$gt" in v and (val is None or val <= v["$gt"]):
                    return False
                if "$lt" in v and (val is None or val >= v["$lt"]):
                    return False
            else:
                if doc.get(k) != v:
                    return False
        return True

    async def insert_one(self, doc):
        stored = copy.deepcopy(doc)
        if "_id" not in stored:
            stored["_id"] = str(len(self.docs) + 1)
        self.docs.append(stored)
        return stored

    async def find_one(self, query=None, projection=None, sort=None):
        query = query or {}
        matching = [d for d in self.docs if self._matches(d, query)]
        if sort:
            key, direction = sort[0]
            matching.sort(key=lambda d: d.get(key, 0) or 0, reverse=(direction == -1))
        if not matching:
            return None
        res = copy.deepcopy(matching[0])
        if projection and projection.get("_id") == 0:
            res.pop("_id", None)
        return res

    def find(self, query=None, projection=None):
        query = query or {}
        matching = [copy.deepcopy(d) for d in self.docs if self._matches(d, query)]
        if projection and projection.get("_id") == 0:
            for d in matching:
                d.pop("_id", None)
        return MockCursor(matching)

    async def update_one(self, query, update, upsert=False):
        for doc in self.docs:
            if self._matches(doc, query):
                if "$set" in update:
                    for k, v in update["$set"].items():
                        doc[k] = v
                if "$inc" in update:
                    for k, v in update["$inc"].items():
                        doc[k] = doc.get(k, 0) + v
                return MockUpdateResult(matched_count=1, modified_count=1)
        if upsert:
            new_doc = copy.deepcopy(query)
            if "$set" in update:
                new_doc.update(update["$set"])
            await self.insert_one(new_doc)
            return MockUpdateResult(matched_count=0, modified_count=1)
        return MockUpdateResult(matched_count=0, modified_count=0)

    async def update_many(self, query, update):
        modified = 0
        for doc in self.docs:
            if self._matches(doc, query):
                if "$set" in update:
                    for k, v in update["$set"].items():
                        doc[k] = v
                if "$inc" in update:
                    for k, v in update["$inc"].items():
                        doc[k] = doc.get(k, 0) + v
                modified += 1
        return MockUpdateResult(matched_count=modified, modified_count=modified)

    async def delete_one(self, query):
        for i, doc in enumerate(self.docs):
            if self._matches(doc, query):
                self.docs.pop(i)
                return True
        return False

    async def delete_many(self, query):
        initial = len(self.docs)
        self.docs = [d for d in self.docs if not self._matches(d, query)]
        return initial - len(self.docs)

    async def count_documents(self, query=None):
        query = query or {}
        return sum(1 for d in self.docs if self._matches(d, query))

    async def create_index(self, keys, **kwargs):
        return True

    def aggregate(self, pipeline):
        return MockCursor([])

class MockDatabase:
    def __init__(self):
        self.collections = {}

    def __getattr__(self, name):
        if name not in self.collections:
            self.collections[name] = MockCollection()
        return self.collections[name]
