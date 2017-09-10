package resty

class Client(val name: String, val phoneNumber: Int) {
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

abstract class ReservationEvent(val client: Client) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReservationEvent) return false

        if (client != other.client) return false

        return true
    }

    override fun hashCode(): Int {
        return client.hashCode()
    }
}

class BookEvent(client: Client, val numberOfGuests: Int, val restaurant: Restaurant) : ReservationEvent(client) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BookEvent) return false
        if (!super.equals(other)) return false

        if (numberOfGuests != other.numberOfGuests) return false
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

class CancelEvent(client: Client) : ReservationEvent(client)
class ReservationCenter(val restaurants: List<Restaurant>) {
    val events: MutableMap<Restaurant, MutableList<ReservationEvent>> = mutableMapOf()

    fun makeReservation(client: Client, numberOfGuests: Int, restaurant: Restaurant): Boolean {
        if (numberOfGuests + countReservation(events[restaurant]) <= restaurant.capacity) {
            if (events[restaurant] == null) {
                val e = mutableListOf<ReservationEvent>()
                events.put(restaurant, e)
            }
            val restaurantEvents : MutableList<ReservationEvent>? = events[restaurant]
            restaurantEvents?.add(BookEvent(client, numberOfGuests, restaurant))
            return true
        }
        return false
    }

    private fun countReservation(restaurantsEvents: List<ReservationEvent>?): Int {
        var currentGuests = 0
        for (event in restaurantsEvents ?: listOf()) {
            if (event is BookEvent)
                currentGuests += event.numberOfGuests
            else if (event is CancelEvent) {
                val initialBookingEvent = restaurantsEvents!!.first { it.client == event.client && it is BookEvent } as BookEvent
                currentGuests -= initialBookingEvent.numberOfGuests
            }
        }
        return currentGuests
    }



    fun cancelReservationEvent(client: Client, numberOfGuests: Int? = null, restaurant: Restaurant? = null): Boolean {
        val restaurant: Restaurant = restaurant ?: findActiveReservation(client) ?: return false
        events[restaurant]?.add(CancelEvent(client))
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