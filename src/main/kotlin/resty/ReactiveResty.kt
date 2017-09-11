package resty

import rx.Observable
import rx.Observer
import rx.functions.Action1
import rx.subjects.ReplaySubject
import rx.subjects.Subject

class User : Observer<InStock> {
    override fun onNext(p0: InStock?) {
        println("Stock is there ! Let's order !")
    }

    override fun onError(p0: Throwable?) {
        println("onError")
    }

    override fun onCompleted() {
        println("onCompleted")
    }
}

class InStock

fun main(vararg args: String) {
    println("Hello, world!")
    val s = ReplaySubject.create<InStock>()
    val s2 = Observable.just(InStock())
    s2.subscribe(User())
//    s2.doOnCompleted { println("just once ?") }
//    s2.doOnNext { println("should have been one only!") }
//    val user = User()
//    s.subscribe(user)
//
//    s.subscribe(object: Action1<InStock> {
//        override fun call(p0: InStock?) {
//            println("Here")
//        }
//    })
//
//    s.subscribe({ println("Here again") } ,{println("oh no")})
//    Thread.sleep(2000)
//    s.onNext(InStock())
//    Thread.sleep(2000)
//    s.onNext(InStock())
//    rx.observables.Observable<ReservationCenter>
}
