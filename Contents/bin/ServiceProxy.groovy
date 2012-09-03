import groovy.transform.TupleConstructor

/**
 * Created with IntelliJ IDEA.
 * User: okrammer
 * Date: 31/08/2012
 * Time: 09:40
 * To change this template use File | Settings | File Templates.
 */
@TupleConstructor
class ServiceProxy {
    Closure handlerClosure

    def methodMissing(String name, Object args) {
        handlerClosure(name, args)
    }
}
