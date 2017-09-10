package resty

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Created by fabien on 10/9/17.
 */

class ReservationCenterTest {

    private val r1 = FrenchRestaurant("Ooolaa Petite", 80)
    private val r2 = FrenchRestaurant("Chez Patrick", 60, byob = true)
    private val r3 = Restaurant("Slims", 20)
    private val r4 = ChineseRestaurant("Lin Heung", 100, englishSpeaking = true)
    lateinit var rc: ReservationCenter

    @Before
    fun before() {
        val restaurants = listOf(r1, r2, r3, r4)
        rc = ReservationCenter(restaurants)
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_make_reservation_when_restaurant_not_exist() {
        val r = Restaurant("Oolaa Petite", 100)
        val fabien = Client("Fabien", 123)

        rc.makeReservation(fabien, 10, r)
    }

    @Test
    fun test_make_reservation_when_possible() {
        val fabien = Client("Fabien", 123)

        val isReserved = rc.makeReservation(fabien, 2, r1)
        assertTrue(isReserved)
        assertTrue(rc.events.containsKey(r1))
        assertTrue(rc.events.containsValue(listOf<ReservationEvent>(BookEvent(fabien, 2, r1))))
    }

    @Test
    fun test_make_reservation_when_capacity_is_reached() {
        val fabien = Client("Fabien", 123)
        val virginie = Client("Virginie", 123)

        rc.makeReservation(fabien, 2, r1)
        val isReserved = rc.makeReservation(virginie, 99, r1)
        assertFalse(isReserved)
        assertTrue(rc.events.containsKey(r1))
        assertTrue(rc.events.containsValue(listOf<ReservationEvent>(BookEvent(fabien, 2, r1))))
    }

    @Test
    fun test_cancel_reservation_when_possible() {
        val fabien = Client("Fabien", 123)
        val virginie = Client("Virginie", 123)

        rc.makeReservation(fabien, 2, r1)
        rc.makeReservation(virginie, 4, r1)

        val isCancelled = rc.cancelReservationEvent(fabien)
        assertTrue(isCancelled)

        assertTrue(rc.events.containsKey(r1))
        val expectedEvents = listOf<ReservationEvent>(BookEvent(fabien, 2, r1), BookEvent(virginie, 4, r1), CancelEvent(fabien,r1))
        assertTrue(expectedEvents == rc.events[r1])
        assertEquals(4, rc.countReservation(r1))
    }

    @Test
    fun test_cancel_reservation_when_nothing_to_cancel() {

    }

}
