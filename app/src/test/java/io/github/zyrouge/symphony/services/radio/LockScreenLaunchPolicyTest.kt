package io.github.zyrouge.symphony.services.radio

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LockScreenLaunchPolicyTest {
    @Test
    fun `modern Android requires overlay permission`() {
        for (sdk in listOf(29, 34, 35, 36)) {
            assertFalse(LockScreenLaunchPolicy.canLaunch(true, true, true, true, true, sdk, false))
            assertTrue(LockScreenLaunchPolicy.canLaunch(true, true, true, true, true, sdk, true))
        }
    }

    @Test
    fun `Android 9 can launch without overlay permission`() {
        assertTrue(LockScreenLaunchPolicy.canLaunch(true, true, true, true, true, 28, false))
    }

    @Test
    fun `disabled paused empty unlocked and calling states suppress launch`() {
        for (disabledGate in 0..4) {
            val gates = MutableList(5) { true }.also { it[disabledGate] = false }
            for (sdk in listOf(28, 34)) {
                assertFalse(LockScreenLaunchPolicy.canLaunch(
                    gates[0], gates[1], gates[2], gates[3], gates[4], sdk, true))
            }
        }
    }
}
