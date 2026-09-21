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
            avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400",
            rating = 5.0,
            balance = 0.0,
            escrowLocked = 0.0,
            fixoPoints = 0,
            verificationStatus = VerificationStatus.VERIFIED_PRO,
            loyaltyTier = LoyaltyTier.BRONZE
        ),
        User(
            id = "usr_worker_1",
            role = UserRole.WORKER,
            name = "Marc Dubois",
            email = "marc.craftsman@fixo.pro",
            phone = "+237 699 876 543",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            rating = 4.96,
            balance = 0.0,
            escrowLocked = 0.0,
            fixoPoints = 0,
            verificationStatus = VerificationStatus.MASTER_CRAFTSMAN,
            loyaltyTier = LoyaltyTier.GOLD
        ),
        User(
            id = "usr_corp_1",
            role = UserRole.ENTERPRISE,
            name = "Apex Metro Contracting Ltd",
            email = "ops@apexmetro.com",
            phone = "+237 650 112 233",
            avatarUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=400",
            rating = 5.0,
            balance = 0.0,
            escrowLocked = 0.0,
            fixoPoints = 0,
            verificationStatus = VerificationStatus.VERIFIED_PRO,
            loyaltyTier = LoyaltyTier.PLATINUM
        ),
        User(
            id = "usr_admin_1",
            role = UserRole.ADMIN,
            name = "Alex Vance (SuperAdmin)",
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
            bio = "Master Plumber with 14 years across residential installations, leak diagnostics, and water supply maintenance. Certified backflow technician with full emergency dispatch in Douala.",
            skills = "Copper & PEX Repiping, Water Heater Overhaul, Hydro-Jetting, Leak Detection, Pressure Regulators",
            certifications = "State Master Plumber #MP-8832, ASSE Certified, Safety Standard L-2",
            completedJobs = 142,
            rating = 4.96,
            reviewCount = 89,
            subscriptionTier = SubscriptionTier.PREMIUM,
            workingDays = "Mon,Tue,Wed,Thu,Fri,Sat",
            slotIntervals = "08:00 - 10:00,10:30 - 12:30,13:30 - 15:30,16:00 - 18:00",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            locationCity = "Douala (Akwa, 1.8 km)",
            locationDistanceKm = 1.8,
            backgroundVerified = true,
            phone = "+237 699 876 543"
        ),
        WorkerProfile(
            id = "wrk_2",
            userId = "usr_worker_2",
            name = "Elena Rostova",
            category = ServiceCategory.ELECTRICAL,
            hourlyRate = 20000.0,
            emergencyCalloutAvailable = true,
            bio = "Master Electrician specializing in circuit breaker upgrades, generator transfer switches, and solar backup systems for residential and commercial spaces.",
            skills = "Main Panel Modernization, Solar Inverters, Circuit Diagnosis, Surge Suppression, Smart Lighting",
            certifications = "Certified High Voltage Electrician #EL-9104, Solar Safety Installer",
            completedJobs = 184,
            rating = 4.98,
            reviewCount = 112,
            subscriptionTier = SubscriptionTier.PRO,
            workingDays = "Mon,Tue,Wed,Thu,Fri",
            slotIntervals = "08:30 - 10:30,11:00 - 13:00,14:00 - 16:00,16:30 - 18:30",
            avatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400",
            locationCity = "Douala (Bonamoussadi, 3.2 km)",
            locationDistanceKm = 3.2,
            backgroundVerified = true,
            phone = "+237 671 445 566"
        ),
        WorkerProfile(
            id = "wrk_3",
            userId = "usr_worker_3",
            name = "Tariq Mansoor",
            category = ServiceCategory.HVAC,
            hourlyRate = 18000.0,
            emergencyCalloutAvailable = true,
            bio = "HVAC technician specializing in split air conditioning maintenance, refrigerant recharge, compressor repairs, and tropical climate cooling optimization.",
            skills = "AC Deep Cleaning, Inverter Compressors, Gas Recharge R410A, Duct Inspection, Thermostats",
            certifications = "Certified Refrigeration Specialist #RF-4421, Universal HVAC Tech",
            completedJobs = 95,
            rating = 4.92,
            reviewCount = 58,
            subscriptionTier = SubscriptionTier.PRO,
            workingDays = "Mon,Tue,Wed,Thu,Fri,Sat",
            slotIntervals = "08:00 - 10:00,10:30 - 12:30,13:30 - 15:30,16:00 - 18:00",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400",
            locationCity = "Yaoundé (Bastos, 4.5 km)",
            locationDistanceKm = 4.5,
            backgroundVerified = true,
            phone = "+237 690 123 789"
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
        ServiceItem("srv_6", "wrk_3", "Split AC Deep Cleaning & Refrigerant Refill", ServiceCategory.HVAC, "Evaporator coil sanitization, gas level check, filter replacement, airflow tuning.", 20000.0, 60),
        ServiceItem("srv_7", "wrk_4", "Custom Hardwood Cabinetry & Shelving", ServiceCategory.CARPENTRY, "Precision joinery custom-fit with solid wood finish.", 50000.0, 180)
    )

    val defaultReels = listOf(
        Reel(
            id = "reel_1",
            workerId = "wrk_1",
            workerName = "Marc Dubois",
            workerAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            title = "High-Pressure Pipe Sealing in Under 5 Minutes",
            description = "Watch this hydraulic joint seal tested at 120 PSI without leaking! 100% watertight guarantee in Douala.",
            category = ServiceCategory.PLUMBING,
            serviceId = "srv_1",
            videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-plumber-tightening-pipes-under-the-sink-41315-large.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1581244277943-fe4a9c777189?w=600",
            likesCount = 428,
            bookingsCount = 39,
            tags = "Plumbing,Hydraulics,Pipes,MasterCraftsman"
        ),
        Reel(
            id = "reel_2",
            workerId = "wrk_2",
            workerName = "Elena Rostova",
            workerAvatar = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400",
            title = "Clean Electrical Distribution Board Redo",
            description = "Clean cable dressing with color-coded breakers and surge arresters. Safety is non-negotiable.",
            category = ServiceCategory.ELECTRICAL,
            serviceId = "srv_4",
            videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-hands-of-an-electrician-fixing-wires-41306-large.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=600",
            likesCount = 812,
            bookingsCount = 74,
            tags = "Electrician,PanelUpgrade,CleanWiring,Douala"
        ),
        Reel(
            id = "reel_3",
            workerId = "wrk_4",
            workerName = "Kofi Mensah",
            workerAvatar = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=400",
            title = "Hand-Chiseled Mortise and Tenon Joint in Solid Iroko",
            description = "Traditional joinery that will outlive generations. Precision friction fit crafted in Bonapriso workshop.",
            category = ServiceCategory.CARPENTRY,
            serviceId = "srv_7",
            videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-carpenter-measuring-and-cutting-a-wood-plank-41300-large.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1538688525198-9b88f6f53126?w=600",
            likesCount = 654,
            bookingsCount = 48,
            tags = "Carpentry,Woodworking,Iroko,HandCrafted"
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
            comment = "Elena was extremely professional, finished in under 2 hours, and explained every breaker circuit. Clean wiring!",
            createdAt = System.currentTimeMillis() - 86400000L
        ),
        com.example.data.model.WorkerReview(
            id = "rev_2",
            bookingId = "bk_prev_1",
            workerId = "wrk_1",
            customerId = "usr_prev_2",
            customerName = "Michel Eto'o",
            rating = 5.0f,
            comment = "Fixed our commercial booster pump during peak business hours without disrupting water to the restaurant.",
            createdAt = System.currentTimeMillis() - 3 * 86400000L
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

    val defaultDisputes = emptyList<DisputeReport>()
}
