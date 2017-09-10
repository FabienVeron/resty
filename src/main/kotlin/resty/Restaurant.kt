package resty

/**
 * Created by fabien on 9/9/17.
 */

open class Restaurant(val name: String, val capacity: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Restaurant

        if (name != other.name) return false
        if (capacity != other.capacity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + capacity
        return result
    }
}

class FrenchRestaurant(name: String, capacity: Int, val byob: Boolean = false) : Restaurant(name, capacity)
class ChineseRestaurant(name: String, capacity: Int, englishSpeaking: Boolean = false) : Restaurant(name, capacity)

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

