package resty

import rx.Observer
import rx.subjects.PublishSubject
import rx.subjects.Subject

class Client(val name: String, val phoneNumber: Int) : Observer<CancelEvent> {
    override fun onError(p0: Throwable?) {
    }

    override fun onNext(p0: CancelEvent?) {
        println("Someone cancelled the restaurant. I can call back")
    }

    override fun onCompleted() {
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Client) return false

        if (name != other.name) return false
        if (phoneNumber != other.phoneNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + phoneNumber
        return result
    }
}

abstract class ReservationEvent(val client: Client, val restaurant: Restaurant) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReservationEvent) return false

        if (client != other.client) return false
        if (restaurant != other.restaurant) return false

        return true
    }

    override fun hashCode(): Int {
        var result = client.hashCode()
        result = 31 * result + restaurant.hashCode()
        return result
    }
}

class BookEvent(client: Client, val numberOfGuests: Int, restaurant: Restaurant) : ReservationEvent(client, restaurant) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BookEvent) return false
        if (!super.equals(other)) return false

//        if (numberOfGuests != other.numberOfGuests) return false
        if (restaurant != other.restaurant) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + numberOfGuests
        result = 31 * result + restaurant.hashCode()
        return result
    }
}

class CancelEvent(client: Client, restaurant: Restaurant) : ReservationEvent(client, restaurant)

class ReservationCenter(val restaurants: List<Restaurant>) {
    val events: MutableMap<Restaurant, MutableList<ReservationEvent>> = mutableMapOf()

    private val cancellations : Subject<CancelEvent,CancelEvent> = PublishSubject.create()

    fun makeReservation(client: Client, numberOfGuests: Int, restaurant: Restaurant,callMeBackWhenCancellation : Boolean = false): Boolean {
        if (!restaurants.contains(restaurant))
            throw IllegalArgumentException("%s is not in the restaurants list".format(restaurant))

        if (numberOfGuests + countReservation(restaurant) <= restaurant.capacity) {
            if (events[restaurant] == null) {
                val e = mutableListOf<ReservationEvent>()
                events.put(restaurant, e)
            }
            val restaurantEvents: MutableList<ReservationEvent>? = events[restaurant]
            restaurantEvents?.add(BookEvent(client, numberOfGuests, restaurant))
            return true
        }
        else if (callMeBackWhenCancellation) {
            cancellations.subscribe(client)
        }
        return false
    }

    fun countReservation(restaurant: Restaurant): Int {
        var currentGuests = 0
        for (event in events[restaurant] ?: listOf<ReservationEvent>()) {
            if (event is BookEvent)
                currentGuests += event.numberOfGuests
            else if (event is CancelEvent) {
                val initialBookingEvent = events[restaurant]!!.first { it.client == event.client && it is BookEvent } as BookEvent
                currentGuests -= initialBookingEvent.numberOfGuests
            }
        }
        return currentGuests
    }

    fun cancelReservationEvent(client: Client, restaurant: Restaurant? = null): Boolean {
        val activeRestaurant: Restaurant = restaurant ?: findActiveReservation(client) ?: return false
        val cancelEvent = CancelEvent(client, restaurant ?: activeRestaurant)
        events[activeRestaurant]?.add(cancelEvent)
        cancellations.onNext(cancelEvent)
        return true
    }

    private fun findActiveReservation(client: Client): Restaurant? {
        for ((restaurant, events) in events.entries) {
            if (events.contains(BookEvent(client, 0, restaurant))) {
                return restaurant
            }
        }
        return null
    }
}