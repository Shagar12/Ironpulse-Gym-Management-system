package com.example

import com.example.data.repository.GymRepository
import com.example.ui.components.calculateBMI
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testBmiCalculation() {
        // Height 178cm, Weight 81kg -> BMI ~ 25.6 (Overweight)
        val info1 = calculateBMI(81.0, 178.0)
        assertEquals(25.6, info1.value, 0.1)
        assertEquals("Overweight", info1.category)

        // Height 165cm, Weight 56kg -> BMI ~ 20.6 (Normal)
        val info2 = calculateBMI(56.0, 165.0)
        assertEquals(20.6, info2.value, 0.1)
        assertEquals("Normal", info2.category)

        // Height 180cm, Weight 92kg -> BMI ~ 28.4 (Overweight)
        val info3 = calculateBMI(92.0, 180.0)
        assertEquals(28.4, info3.value, 0.1)
        assertEquals("Overweight", info3.category)
    }

    @Test
    fun testMacroCalculationsForPhases() {
        val bw = 80.0
        val cutting = GymRepository.calculateMacrosForPhase(bw, "Cutting")
        assertEquals(2240, cutting.first) // 80 * 28
        assertEquals(176, cutting.second) // 80 * 2.2
        assertEquals(56, cutting.fourth) // 80 * 0.7
        assertTrue(cutting.third > 0)

        val bulking = GymRepository.calculateMacrosForPhase(bw, "Bulking")
        assertEquals(3040, bulking.first) // 80 * 38
        assertEquals(160, bulking.second) // 80 * 2.0
        assertEquals(80, bulking.fourth) // 80 * 1.0
        assertTrue(bulking.third > 0)
    }

    @Test
    fun testMemberSignupPendingAndCoachApproval() {
        // Newly signed up applicant starts in pending approval state
        val applicant = com.example.data.model.MemberEntity(
            id = "client_test_01",
            name = "Test Applicant",
            email = "test@fitgym.com",
            role = "MEMBER",
            gender = "Male",
            age = 26,
            goalPhase = "Cutting",
            challengeLevel = "Intermediate",
            currentWeight = 80.0,
            targetWeight = 75.0,
            heightCm = 180.0,
            dailyCalories = 2240,
            proteinG = 176,
            carbsG = 250,
            fatG = 56,
            approved = false, // Pending coach review
            atGym = false,
            subscriptionDaysLeft = 30
        )
        // Verify initial state is unapproved
        org.junit.Assert.assertFalse(applicant.approved)
        assertEquals("MEMBER", applicant.role)

        // Coach approves as MEMBER
        val approvedAsMember = applicant.copy(approved = true, role = "MEMBER")
        assertTrue(approvedAsMember.approved)
        assertEquals("MEMBER", approvedAsMember.role)

        // Coach approves as COACH (Staff privileges)
        val approvedAsCoach = applicant.copy(approved = true, role = "COACH")
        assertTrue(approvedAsCoach.approved)
        assertEquals("COACH", approvedAsCoach.role)

        // Dynamic live role promotion/demotion
        val promoted = approvedAsMember.copy(role = "COACH")
        assertEquals("COACH", promoted.role)
        val reverted = promoted.copy(role = "MEMBER")
        assertEquals("MEMBER", reverted.role)
    }
}
