package resty

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Created by fabien on 10/9/17.
 */

class ReservationCenterTest {

    lateinit var rc: ReservationCenter

    @Before
    fun before() {
        val r1 = FrenchRestaurant("Ooolaa Petite", 80)
        val r2 = FrenchRestaurant("Chez Patrick", 60, byob = true)
        val r3 = Restaurant("Slims", 20)
        val r4 = ChineseRestaurant("Lin Heung", 100, englishSpeaking = true)
        val restaurants = listOf(r1, r2, r3, r4)
        rc = ReservationCenter(restaurants)
    }

    @Test
    fun test_make_reservation_when_possible() {
        val oolaaPetite = Restaurant("Oolaa Petite", 100)
        val fabien = Client("Fabien", 123)

        val isReserved = rc.makeReservation(fabien, 2, oolaaPetite)
        assertTrue(isReserved)
        assertTrue(rc.events.containsKey(oolaaPetite))
        assertTrue(rc.events.containsValue(listOf<ReservationEvent>(BookEvent(fabien, 2, oolaaPetite))))
    }

    @Test
    fun test_make_reservation_when_capacity_is_reached() {
        val oolaaPetite = Restaurant("Oolaa Petite", 100)
        val fabien = Client("Fabien", 123)
        val virginie = Client("Virginie", 123)

        rc.makeReservation(fabien, 2, oolaaPetite)
        val isReserved = rc.makeReservation(virginie, 99, oolaaPetite)
        assertFalse(isReserved)
        assertTrue(rc.events.containsKey(oolaaPetite))
        assertTrue(rc.events.containsValue(listOf<ReservationEvent>(BookEvent(fabien, 2, oolaaPetite))))
    }
}
