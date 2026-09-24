package com.example.data.local

import com.example.data.model.Booking
import com.example.data.model.ChatMessage
import com.example.data.model.DisputeReport
import com.example.data.model.EnterpriseProject
import com.example.data.model.EscrowStatus
import com.example.data.model.JobStatus
import com.example.data.model.LoyaltyTier
import com.example.data.model.PaymentMethod
import com.example.data.model.Reel
import com.example.data.model.RewardItem
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.SubscriptionTier
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.data.model.WalletTransaction
import com.example.data.model.WorkerProfile

/**
 * Isolated Development Fixtures for FIXO (Cameroon / Central Africa Region).
 * All balances start at 0 FCFA to ensure absolute financial ledger integrity.
 * Currency is denominated in XAF (FCFA).
 */
object FixoSeedData {

    val defaultUsers = listOf(
        User(
            id = "usr_cust_1",
            role = UserRole.CUSTOMER,
            name = "Sarah Jenkins",
            email = "sarah.j@gmail.com",
            phone = "+237 671 234 567",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300",
            rating = 5.0,
            balance = 35000.0,
            escrowLocked = 0.0,
            fixoPoints = 350,
            verificationStatus = VerificationStatus.VERIFIED_PRO,
            loyaltyTier = LoyaltyTier.GOLD,
            city = "Douala",
            quarter = "Bonapriso"
        ),
        User(
            id = "usr_worker_1",
            role = UserRole.WORKER,
            name = "Marc Dubois",
            email = "marc.craftsman@fixo.pro",
            phone = "+237 699 876 543",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300",
            rating = 4.9,
            balance = 48500.0,
            escrowLocked = 15000.0,
            fixoPoints = 0,
            verificationStatus = VerificationStatus.MASTER_CRAFTSMAN,
            loyaltyTier = LoyaltyTier.GOLD,
            city = "Douala",
            quarter = "Akwa"
        ),
        User(
            id = "usr_worker_paul",
            role = UserRole.WORKER,
            name = "Paul Essomba",
            email = "paul.electric@fixo.pro",
            phone = "+237 670 112 233",
            avatarUrl = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=300",
            rating = 4.8,
            balance = 34000.0,
            escrowLocked = 0.0,
            fixoPoints = 120,
            verificationStatus = VerificationStatus.VERIFIED_PRO,
            loyaltyTier = LoyaltyTier.SILVER,
            city = "Douala",
            quarter = "Deido"
        ),
        User(
            id = "usr_corp_1",
            role = UserRole.ENTERPRISE,
            name = "Apex Metro Contracting Ltd",
            email = "ops@apexmetro.com",
            phone = "+237 650 112 233",
            avatarUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=400",
            rating = 5.0,
            balance = 120000.0,
            escrowLocked = 25000.0,
            fixoPoints = 0,
            verificationStatus = VerificationStatus.VERIFIED_PRO,
            loyaltyTier = LoyaltyTier.PLATINUM
        ),
        User(
            id = "usr_admin_1",
            role = UserRole.ADMIN,
            name = "Administrateur KYC & Litiges",
            email = "compliance@fixo.internal",
            phone = "+237 670 001 122",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
            rating = 5.0,
            balance = 0.0,
            escrowLocked = 0.0,
            fixoPoints = 0,
            verificationStatus = VerificationStatus.MASTER_CRAFTSMAN,
            loyaltyTier = LoyaltyTier.PLATINUM
        )
    )

    val defaultWorkers = listOf(
        WorkerProfile(
            id = "wrk_1",
            userId = "usr_worker_1",
            name = "Marc Dubois",
            category = ServiceCategory.PLUMBING,
            hourlyRate = 15000.0,
            emergencyCalloutAvailable = true,
            bio = "Maître Plombier certifié FIXO avec 14 ans d'expérience. Diagnostic rapide, soudures cuivre, étanchéité haute pression et dépannage d'urgence sous 30 minutes à Douala.",
            skills = "Plomberie sanitaire, Soudure cuivre, Détection de fuites",
            certifications = "Identité Biométrique Validée (CNI), Casier Judiciaire Vierge (Bulletin n°3), Compétences Techniques Certifiées, Couverture Fixo Shield",
            completedJobs = 142,
            rating = 4.9,
            reviewCount = 124,
            subscriptionTier = SubscriptionTier.PREMIUM,
            workingDays = "Mon,Tue,Wed,Thu,Fri,Sat",
            slotIntervals = "08:00 - 10:00,10:30 - 12:30,13:30 - 15:30,16:00 - 18:00",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300",
            locationCity = "Douala (Akwa, 1.2 km)",
            locationDistanceKm = 1.2,
            backgroundVerified = true,
            phone = "+237 699 876 543"
        ),
        WorkerProfile(
            id = "wrk_2",
            userId = "usr_worker_paul",
            name = "Paul Essomba",
            category = ServiceCategory.ELECTRICAL,
            hourlyRate = 20000.0,
            emergencyCalloutAvailable = true,
            bio = "Artisan Électricien Pro expert en raccordements triphasés, tableaux divisionnaires, inverseurs de groupes et sécurisation aux normes NF C 15-100.",
            skills = "Électricité bâtiment, Groupes électrogènes, Tableaux divisionnaires",
            certifications = "Identité Biométrique Validée (CNI), Casier Judiciaire Vierge (Bulletin n°3), Compétences Techniques Certifiées, Couverture Fixo Shield",
            completedJobs = 84,
            rating = 4.8,
            reviewCount = 67,
            subscriptionTier = SubscriptionTier.PRO,
            workingDays = "Mon,Tue,Wed,Thu,Fri",
            slotIntervals = "08:30 - 10:30,11:00 - 13:00,14:00 - 16:00,16:30 - 18:30",
            avatarUrl = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=300",
            locationCity = "Douala (Deido, 2.4 km)",
            locationDistanceKm = 2.4,
            backgroundVerified = true,
            phone = "+237 670 112 233"
        ),
        WorkerProfile(
            id = "wrk_3",
            userId = "usr_worker_eric",
            name = "Eric Kamga",
            category = ServiceCategory.AC_COOLING,
            hourlyRate = 18000.0,
            emergencyCalloutAvailable = true,
            bio = "Technicien Frigoriste certifié avec 9 ans d'expérience à Douala. Entretien haute pression, désembouage split et recharge gaz R410A.",
            skills = "Climatisation Split, Désembouage haute pression, Recharge R410A, Évacuation condensats",
            certifications = "Identité Biométrique Validée (CNI), Casier Judiciaire Vierge (Bulletin n°3), Compétences Techniques Certifiées, Couverture Fixo Shield",
            completedJobs = 98,
            rating = 4.9,
            reviewCount = 42,
            subscriptionTier = SubscriptionTier.PRO,
            workingDays = "Mon,Tue,Wed,Thu,Fri,Sat",
            slotIntervals = "08:00 - 10:00,10:30 - 12:30,13:30 - 15:30,16:00 - 18:00",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
            locationCity = "Douala (Bonanjo, 1.8 km)",
            locationDistanceKm = 1.8,
            backgroundVerified = true,
            phone = "+237 671 998 877"
        ),
        WorkerProfile(
            id = "wrk_4",
            userId = "usr_worker_4",
            name = "Kofi Mensah",
            category = ServiceCategory.CARPENTRY,
            hourlyRate = 14000.0,
            emergencyCalloutAvailable = false,
            bio = "Master Carpenter crafting solid hardwood furniture, architectural doors, custom kitchen cabinetry, and precision framing repairs.",
            skills = "Custom Cabinetry, Solid Iroko & Teak Joinery, Door Realignment, Architectural Shelving",
            certifications = "Master Artisan Wood Guildsman #AWI-209",
            completedJobs = 110,
            rating = 4.94,
            reviewCount = 64,
            subscriptionTier = SubscriptionTier.STARTER,
            workingDays = "Mon,Tue,Wed,Thu,Fri",
            slotIntervals = "09:00 - 11:00,11:30 - 13:30,14:30 - 16:30",
            avatarUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=400",
            locationCity = "Douala (Bonapriso, 2.1 km)",
            locationDistanceKm = 2.1,
            backgroundVerified = true,
            phone = "+237 677 334 455"
        )
    )

    val defaultServices = listOf(
        ServiceItem("srv_1", "wrk_1", "Emergency Water Leak & Pipe Isolation", ServiceCategory.PLUMBING, "Immediate location and isolation of burst pipes, copper soldering, PEX replacement.", 15000.0, 45),
        ServiceItem("srv_2", "wrk_1", "Water Heater Installation & Descaling", ServiceCategory.PLUMBING, "Flush heat exchangers, replace heating elements, install high-efficiency units.", 25000.0, 120),
        ServiceItem("srv_3", "wrk_1", "High-Pressure Drain & Sewer Clearance", ServiceCategory.PLUMBING, "High-pressure unclogging with full digital camera inspection.", 35000.0, 90),
        ServiceItem("srv_4", "wrk_2", "Electrical Panel Modernization & Breakers", ServiceCategory.ELECTRICAL, "Upgrade vintage fuse boxes to modern circuit breakers with full surge protection.", 45000.0, 240),
        ServiceItem("srv_5", "wrk_2", "Generator Transfer Switch & Solar Backup", ServiceCategory.ELECTRICAL, "Dedicated manual/auto transfer switch setup for emergency power.", 35000.0, 120),
        ServiceItem("srv_6", "wrk_3", "Nettoyage approfondi circuit split frigorifique", ServiceCategory.AC_COOLING, "Evaporator coil sanitization, gas level check, filter replacement, airflow tuning.", 18000.0, 60),
        ServiceItem("srv_7", "wrk_4", "Custom Hardwood Cabinetry & Shelving", ServiceCategory.CARPENTRY, "Precision joinery custom-fit with solid wood finish.", 50000.0, 180)
    )

    val defaultReels = listOf(
        Reel(
            id = "reel_plumbing_01",
            workerId = "wrk_1",
            workerName = "Marc Dubois",
            workerAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300",
            title = "Remplacement sous pression d'une vanne d'arrêt défectueuse sans couper l'eau de l'immeuble.",
            description = "Intervention d'urgence à Akwa: remplacement rapide sous pression d'une vanne bloquée sans interrompre l'eau des voisins.",
            category = ServiceCategory.PLUMBING,
            serviceId = "srv_1",
            videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-plumber-tightening-a-pipe-fitting-41484-large.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1581244277943-fe4a9c777189?w=600",
            likesCount = 428,
            bookingsCount = 39,
            tags = "Plomberie,SoudurePEX,UrgenceDouala",
            priceAmount = 15000.0,
            location = "Akwa, Douala",
            reviewsCount = 84
        ),
        Reel(
            id = "reel_electric_02",
            workerId = "wrk_2",
            workerName = "Paul Essomba",
            workerAvatar = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=300",
            title = "Câblage propre et équilibrage de phases sur un disjoncteur différentiel 30mA",
            description = "Tableau divisionnaire haute sécurité à Deido: passage en peignes cuivre et calibrage différentiel 30mA.",
            category = ServiceCategory.ELECTRICAL,
            serviceId = "srv_4",
            videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-hands-of-an-electrician-working-with-wires-41483-large.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=600",
            likesCount = 312,
            bookingsCount = 67,
            tags = "Électricité,Tableau,NormesNFC",
            priceAmount = 20000.0,
            location = "Deido, Douala",
            reviewsCount = 67
        ),
        Reel(
            id = "reel_ac_03",
            workerId = "wrk_3",
            workerName = "Eric Kamga",
            workerAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
            title = "Nettoyage approfondi circuit split frigorifique et recharge gaz R410A",
            description = "Désembouage haute pression de l'évaporateur et recharge fluide frigorifique pour climatiseur split à Bonanjo.",
            category = ServiceCategory.AC_COOLING,
            serviceId = "srv_6",
            videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-man-working-with-air-conditioning-unit-41481-large.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1621905252507-b35492cc74b4?w=600",
            likesCount = 524,
            bookingsCount = 42,
            tags = "Climatisation,SplitFrigorifique,FroidDouala",
            priceAmount = 18000.0,
            location = "Bonanjo, Douala",
            reviewsCount = 42
        )
    )

    // Sandbox Bookings for immediate testing and demonstration in Cameroon
    val defaultBookings = listOf(
        Booking(
            id = "bk_douala_active",
            customerId = "usr_cust_1",
            customerName = "Sarah Jenkins",
            workerId = "wrk_1",
            workerName = "Marc Dubois",
            workerAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            serviceTitle = "Emergency Water Leak & Pipe Isolation",
            category = ServiceCategory.PLUMBING,
            date = "Today",
            timeSlot = "Immediate (Emergency Callout)",
            status = JobStatus.ON_THE_WAY,
            address = "Rue Drouot, Akwa, Douala",
            notes = "Main valve isolated; burst copper pipe under kitchen counter. Need immediate welding and pressure check.",
            priceAmount = 15000.0,
            escrowStatus = EscrowStatus.HOLDING,
            paymentMethod = PaymentMethod.MTN_MOMO,
            customerRating = 0f,
            customerReviewText = "",
            pointsEarned = 0,
            workerPhone = "+237 699 876 543",
            customerLat = 4.0511,
            customerLng = 9.7085,
            workerLat = 4.0380,
            workerLng = 9.6990,
            trackingActive = true,
            etaMinutes = 8,
            distanceKm = 2.1,
            workerSpeedKmh = 26.5f,
            workerHeading = 35.0f,
            lastLocationUpdate = System.currentTimeMillis() - 45000L
        ),
        Booking(
            id = "bk_douala_completed",
            customerId = "usr_cust_1",
            customerName = "Sarah Jenkins",
            workerId = "wrk_2",
            workerName = "Elena Rostova",
            workerAvatar = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400",
            serviceTitle = "Electrical Panel Modernization & Breakers",
            category = ServiceCategory.ELECTRICAL,
            date = "Yesterday",
            timeSlot = "10:30 - 12:30",
            status = JobStatus.COMPLETED,
            address = "Avenue Charles de Gaulle, Bonanjo, Douala",
            notes = "Upgraded vintage fuse board to modern Schneider RCBOs and added surge protection.",
            priceAmount = 45000.0,
            escrowStatus = EscrowStatus.RELEASED,
            paymentMethod = PaymentMethod.FIXO_WALLET,
            customerRating = 5.0f,
            customerReviewText = "Elena was extremely professional, finished in under 2 hours, and explained every breaker circuit. Clean wiring!",
            pointsEarned = 50,
            workerPhone = "+237 670 445 566"
        )
    )

    val defaultChat = listOf(
        ChatMessage(
            id = "msg_init_1",
            bookingId = "bk_douala_active",
            senderId = "wrk_1",
            senderName = "Marc Dubois",
            senderRole = UserRole.WORKER,
            message = "Bonjour Sarah, I have accepted your emergency request. My truck is packed with copper couplings and a leak detector. Heading your way now!"
        ),
        ChatMessage(
            id = "msg_init_2",
            bookingId = "bk_douala_active",
            senderId = "usr_cust_1",
            senderName = "Sarah Jenkins",
            senderRole = UserRole.CUSTOMER,
            message = "Thank you Marc! The gate code is #402. Call me as soon as you reach Rue Drouot."
        ),
        ChatMessage(
            id = "msg_init_3",
            bookingId = "bk_douala_active",
            senderId = "wrk_1",
            senderName = "Marc Dubois",
            senderRole = UserRole.WORKER,
            message = "Understood. I am passing Boulevard de la Liberté right now, estimated arrival in 8 minutes."
        )
    )

    val defaultLocations = listOf(
        com.example.data.model.WorkerLocation(
            bookingId = "bk_douala_active",
            workerId = "wrk_1",
            latitude = 4.0380,
            longitude = 9.6990,
            speedKmh = 26.5f,
            heading = 35.0f,
            destinationLat = 4.0511,
            destinationLng = 9.7085,
            destinationAddress = "Rue Drouot, Akwa, Douala",
            isTrackingActive = true,
            updatedAt = System.currentTimeMillis() - 45000L
        )
    )

    val defaultNotifications = listOf(
        com.example.data.model.FixoNotification(
            id = "notif_1",
            userId = "usr_cust_1",
            title = "Artisan Started Trip",
            message = "Marc Dubois (Plumber) is on the way to Rue Drouot. Estimated arrival in 8 mins.",
            type = "WORKER_STARTED_TRIP",
            bookingId = "bk_douala_active",
            timestamp = System.currentTimeMillis() - 5 * 60000L,
            isRead = false
        ),
        com.example.data.model.FixoNotification(
            id = "notif_2",
            userId = "usr_cust_1",
            title = "Escrow Payment Held",
            message = "15,000 FCFA successfully held in FIXO Escrow for job #bk_douala_active.",
            type = "PAYMENT",
            bookingId = "bk_douala_active",
            timestamp = System.currentTimeMillis() - 10 * 60000L,
            isRead = true
        ),
        com.example.data.model.FixoNotification(
            id = "notif_3",
            userId = "usr_cust_1",
            title = "Job Completed & Escrow Released",
            message = "Elena Rostova completed job #bk_douala_completed. +50 FIXO Points earned!",
            type = "JOB_COMPLETED",
            bookingId = "bk_douala_completed",
            timestamp = System.currentTimeMillis() - 86400000L,
            isRead = true
        )
    )

    val defaultReviews = listOf(
        com.example.data.model.WorkerReview(
            id = "rev_1",
            bookingId = "bk_douala_completed",
            workerId = "wrk_2",
            customerId = "usr_cust_1",
            customerName = "Sarah Jenkins",
            rating = 5.0f,
            comment = "Paul a équilibré les 3 phases de notre tableau électrique et installé le disjoncteur différentiel 30mA avec une propreté chirurgicale !",
            createdAt = System.currentTimeMillis() - 86400000L
        ),
        com.example.data.model.WorkerReview(
            id = "rev_2",
            bookingId = "bk_prev_1",
            workerId = "wrk_1",
            customerId = "usr_prev_2",
            customerName = "Michel Eto'o",
            rating = 5.0f,
            comment = "Remplacement sous pression de la vanne générale à Akwa réussi à 100% sans couper l'eau de l'immeuble. Diagnostic impeccable.",
            createdAt = System.currentTimeMillis() - 3 * 86400000L
        ),
        com.example.data.model.WorkerReview(
            id = "rev_3",
            bookingId = "bk_prev_2",
            workerId = "wrk_1",
            customerId = "usr_prev_3",
            customerName = "Jeanne Manga",
            rating = 4.9f,
            comment = "Soudure cuivre propre sur tuyau haute pression sous évier. Arrivé en 20 minutes chrono.",
            createdAt = System.currentTimeMillis() - 5 * 86400000L
        ),
        com.example.data.model.WorkerReview(
            id = "rev_4",
            bookingId = "bk_prev_3",
            workerId = "wrk_3",
            customerId = "usr_prev_4",
            customerName = "Alain Fotso",
            rating = 5.0f,
            comment = "Désembouage complet du split et recharge gaz R410A. Température glaciale retrouvée en 45 minutes !",
            createdAt = System.currentTimeMillis() - 2 * 86400000L
        )
    )

    val defaultTransactions = listOf(
        WalletTransaction(
            id = "tx_esc_1",
            userId = "usr_cust_1",
            type = "ESCROW_HOLD",
            amount = -15000.0,
            currency = "XAF",
            description = "Escrow hold for Emergency Plumbing Callout (Marc Dubois)",
            status = "COMPLETED",
            paymentProvider = "MTN_MOMO",
            referenceCode = "ESC-DOUALA-01",
            timestamp = System.currentTimeMillis() - 10 * 60000L
        )
    )

    val defaultRewards = listOf(
        RewardItem("rew_1", "2,500 FCFA Off Emergency Callout", "Valid for any emergency plumbing or electrical dispatch", 250, 2500.0, "EMERGENCY25"),
        RewardItem("rew_2", "5,000 FCFA Off AC or Electrical Service", "Applies to quotes exceeding 25,000 FCFA with verified master artisans", 450, 5000.0, "MASTER50"),
        RewardItem("rew_3", "Free Home Electrical Safety Audit", "Full circuit diagnostic voucher", 700, 15000.0, "AUDITFREE")
    )

    val defaultEnterpriseProjects = listOf(
        EnterpriseProject("ent_1", "Apex Metro Contracting Ltd", "Commercial Plaza Lighting Overhaul", 6, ServiceCategory.ELECTRICAL, 650000.0, "ACTIVE", "Douala Business Center", 4),
        EnterpriseProject("ent_2", "Grand Horizon Residences", "Sanitary PEX Re-Pipe Project", 10, ServiceCategory.PLUMBING, 1450000.0, "OPEN", "Yaoundé Bastos Residences", 3)
    )

    val defaultOrganizations = listOf(
        com.example.data.model.Organization(
            id = "org_1",
            name = "Bâtir Cameroon SARL",
            type = "Construction & Civil Engineering",
            description = "Leading multi-disciplinary construction, structural masonry, and turn-key residential renovation contractor in Central Africa with over 15 years operating history.",
            logoUrl = "https://images.unsplash.com/photo-1541888946425-d0fbb186156f?w=400",
            phone = "+237 670 112 233",
            email = "contact@batir-cameroon.cm",
            address = "Boulevard de la Liberté, Akwa",
            city = "Douala",
            region = "Littoral",
            registrationNumber = "RC/DLA/2012/B/4521 - NIU M02120003412P",
            authorizedRepresentative = "Ing. Patrick Ndjock",
            representativeTitle = "Chief Executive Engineer",
            verificationStatus = VerificationStatus.VERIFIED_PRO,
            rating = 4.9,
            completedProjectsCount = 38,
            activeWorkersCount = 45,
            website = "https://batir-cameroon.cm",
            servicesOffered = "Structural Masonry, Full Home Renovation, Commercial Roofing, High-Pressure Plumbing Networks"
        ),
        com.example.data.model.Organization(
            id = "org_2",
            name = "Douala Électro-Clim Services",
            type = "HVAC & Industrial Electrical",
            description = "Specialized industrial air conditioning, cold storage installations, and commercial high-voltage wiring for commercial plazas and residential villas.",
            logoUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=400",
            phone = "+237 690 445 566",
            email = "services@electroclim-douala.com",
            address = "Rue Joss, Bonanjo",
            city = "Douala",
            region = "Littoral",
            registrationNumber = "RC/DLA/2018/B/8912 - NIU M05180009811T",
            authorizedRepresentative = "Mme. Claire Tchouaffe",
            representativeTitle = "Operations Director",
            verificationStatus = VerificationStatus.VERIFIED_PRO,
            rating = 4.8,
            completedProjectsCount = 24,
            activeWorkersCount = 22,
            website = "https://electroclim-douala.com",
            servicesOffered = "HVAC Chiller Installation, Backup Generator Wiring, Smart Inverter Split Maintenance, Solar Inverter Micro-Grids"
        ),
        com.example.data.model.Organization(
            id = "org_3",
            name = "Yaoundé Finition & Déco",
            type = "Architectural Finishing & Carpentry",
            description = "Luxury hardwood cabinetry, plasterboard ceiling moulding, tile paving, and waterproof micro-cement finishes.",
            logoUrl = "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=400",
            phone = "+237 655 778 899",
            email = "info@yde-finition.cm",
            address = "Montée Ane Rouge, Centre-Ville",
            city = "Yaoundé",
            region = "Centre",
            registrationNumber = "RC/YAO/2016/B/3110 - NIU M08160007622L",
            authorizedRepresentative = "M. Rodrigue Kamga",
            representativeTitle = "Managing Master Artisan",
            verificationStatus = VerificationStatus.VERIFIED_PRO,
            rating = 5.0,
            completedProjectsCount = 42,
            activeWorkersCount = 28,
            website = "https://yde-finition.cm",
            servicesOffered = "Bespoke Hardwood Kitchens, Gypsum Board Acoustic Ceilings, Anti-Slip Terracotta Paving, Fine Acrylic Emulsion Painting"
        )
    )

    val defaultWorkforceRequests = listOf(
        com.example.data.model.WorkforceRequest(
            id = "wfr_1",
            organizationId = "org_1",
            organizationName = "Bâtir Cameroon SARL",
            projectTitle = "Commercial Office Tower Renovation",
            category = ServiceCategory.ELECTRICAL,
            requiredCount = 8,
            recruitedCount = 5,
            ratePerDayXaf = 25000.0,
            location = "Bonanjo, Douala",
            startDate = "Next Monday",
            endDate = "3 Weeks",
            status = "OPEN",
            description = "Seeking 3 certified electricians experienced in cable-tray routing, main distribution panel wiring, and emergency lighting circuits."
        ),
        com.example.data.model.WorkforceRequest(
            id = "wfr_2",
            organizationId = "org_1",
            organizationName = "Bâtir Cameroon SARL",
            projectTitle = "Residential Villa Hydro Plumbing",
            category = ServiceCategory.PLUMBING,
            requiredCount = 5,
            recruitedCount = 3,
            ratePerDayXaf = 20000.0,
            location = "Bonamoussadi, Douala",
            startDate = "Immediate",
            endDate = "10 Days",
            status = "OPEN",
            description = "Need licensed plumbers for Multilayer PPR and PEX pipe press fittings, booster pumps, and underground drainage slope inspection."
        ),
        com.example.data.model.WorkforceRequest(
            id = "wfr_3",
            organizationId = "org_2",
            organizationName = "Douala Électro-Clim Services",
            projectTitle = "Shopping Mall VRV Air Conditioning Setup",
            category = ServiceCategory.AC_COOLING,
            requiredCount = 6,
            recruitedCount = 2,
            ratePerDayXaf = 30000.0,
            location = "Akwa Mall, Douala",
            startDate = "1st of next month",
            endDate = "1 Month",
            status = "OPEN",
            description = "Recruiting HVAC technicians for refrigerant R410a copper brazing, vacuum testing, and central cassette duct installations."
        )
    )

    val defaultDisputes = emptyList<DisputeReport>()
}
