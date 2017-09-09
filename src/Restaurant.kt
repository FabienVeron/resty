/**
 * Created by fabien on 9/9/17.
 */

open class Restaurant(val name: String, val capacity: Int)

class FrenchRestaurant(name: String, capacity: Int, val byob: Boolean = false) : Restaurant(name, capacity)
class ChineseRestaurant(name: String, capacity: Int, englishSpeaking: Boolean = false) : Restaurant(name, capacity)

class Client(val name: String, val phoneNumber: Int)

class Reservation(val client: Client, val numberOfGuests: Int? = null) {
    override fun equals(other: Any?): Boolean {
        if (other !is Reservation) return false
        return (this.client == other.client && this.numberOfGuests == other.numberOfGuests ?: this.numberOfGuests)
    }
}

open class ReservationEvent(val client: Client) {

}
class BookEvent(client: Client, val numberOfGuests: Int, val restaurant: Restaurant) : ReservationEvent(client)
class CancelEvent(client: Client) : ReservationEvent(client)

val allReservations: MutableMap<Restaurant, MutableList<Reservation>> = mutableMapOf()
val allReservationEvents: MutableMap<Restaurant, MutableList<ReservationEvent>> = mutableMapOf()

fun capacity(restaurants: List<Restaurant>): Int {
    return restaurants.sumBy { it.capacity }
}

fun frenchRestaurantsByob(restaurants: List<Restaurant>): List<FrenchRestaurant> {
    val res: MutableList<FrenchRestaurant> = mutableListOf()
    for (r: Restaurant in restaurants) {
        if (r is FrenchRestaurant) {
            if (r.byob) {
                res.add(r)
            }
        }
    }
    return res
}

val r1 = FrenchRestaurant("Ooola Petite", 80)
val r2 = FrenchRestaurant("Chez Patrick", 60, byob = true)
val r3 = Restaurant("Slims", 20)
val r4 = ChineseRestaurant("Lin Heung", 100, englishSpeaking = true)
val restaurants = listOf(r1, r2, r3, r4)

fun makeReservationEvent(client: Client, numberOfGuests: Int, restaurant: Restaurant): Boolean {
    val restaurantsEvents = allReservationEvents[restaurant]?: allReservationEvents.put(restaurant,mutableListOf())

    if (numberOfGuests + countReservation(restaurantsEvents) <= restaurant.capacity) {
        restaurantsEvents!!.add(BookEvent(client,numberOfGuests,restaurant))
        return true
    }
    return false
}

fun cancelReservationEvent(client:Client, numberOfGuests:Int? = null, restaurant:Restaurant? = null) : Boolean {
    val restaurant :Restaurant = restaurant?:findActiveReservation(client) ?: return false
    allReservationEvents[restaurant]?.add(CancelEvent(client))
    return true
}

fun findActiveReservation(client: Client): Restaurant? {
    for((restaurant,events) in allReservationEvents.entries) {
        if (events.contains(BookEvent(client,0,restaurant))) {
            return restaurant
        }
    }
    return null
}

private fun countReservation(restaurantsEvents: List<ReservationEvent>?) : Int {
    var currentGuests = 0
    for (event in restaurantsEvents?: listOf()) {
        if (event is BookEvent)
            currentGuests += event.numberOfGuests
        else if (event is CancelEvent) {
            val initialBookingEvent = restaurantsEvents!!.first { it.client == event.client && it is BookEvent} as BookEvent
            currentGuests -= initialBookingEvent.numberOfGuests
        }
    }
    return currentGuests
}

fun makeReservation(client: Client, numberOfGuests: Int, restaurant: Restaurant): Boolean {
    if (allReservations[restaurant] == null) {
        allReservations.put(restaurant, mutableListOf())
    }

    val restaurantReservation: MutableList<Reservation> = allReservations[restaurant]!!
    val totalGuest: Int = restaurantReservation.sumBy { it.numberOfGuests ?: 0 }
    if (totalGuest + numberOfGuests <= restaurant.capacity) {
        restaurantReservation.add(Reservation(client, numberOfGuests))
        return true
    }
    return false
}

fun cancelReservation(client: Client) {
    for ((restaurant, reservations) in allReservations.entries) {
        if (reservations.contains(Reservation(client))) {
            reservations.remove(Reservation(client))
        }
    }
}

fun main(args: Array<String>) {

    println("1. HK restaurants capacity: %d".format(capacity(restaurants)))
    println("2. French restaurants BYOB capacity: %d".format(capacity(frenchRestaurantsByob(restaurants))))

    makeReservation(Client("Virginie", 123123), numberOfGuests = 3, restaurant = r1)
    cancelReservation(Client("Virginie", 123123))

    makeReservationEvent(Client("Fabien",1234),numberOfGuests = 4,restaurant = r2)
    cancelReservationEvent(Client("Fabien",1234))
}