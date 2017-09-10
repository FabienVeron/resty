package resty

interface Observer<in E>{
    fun update(event : E)
}

abstract class Observable<E> {
    private val observers: MutableList<Observer<E>> = mutableListOf()

    fun registerObserver(observer: Observer<E>) {
        observers.add(observer)
    }

    fun notifyObservers(event: E) {
        for (observer in observers) {
            observer.update(event)
        }
    }
}