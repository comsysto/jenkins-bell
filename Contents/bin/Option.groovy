abstract class Option<T> {


    def static <T> Option<T> option(Collection<T> collection){
        collection.isEmpty() ? None.none() : new Some(collection.iterator().next())
    }

    def static <T> Option<T> option(Object object){
        object ? new Some(object) : None.none()
    }

    def static <E> None<E> none(){
        return None.NONE as None<E>
    }

    def static <T> Some<T> some(T value) {
        new Some<T>(value)
    }


    abstract Iterator<T> iterator()

    abstract <E> Option<E> map(Closure<E> c);

    abstract def <F, E> Option<F> rightShiftUnsigned(Option<E> other, Closure<F> combine)

    abstract boolean isSome()

    abstract boolean isNone()

    abstract Option flatten()

    abstract Option<T> ifNoneThanSome(Closure<T> c)

    def <E> E defaultOrMap(E defaultValue, Closure<E> c){
        if (isNone()) defaultValue
        else c(value)
    }

    def <E> void ifSome(Closure<Void> c){
        if (isNone()) return
        c(value)
    }

    def List toList() {
        def option = flatten()
        if (option.isNone()) return []

        option.value.flatten()
    }

    static class None<T> extends Option<T> {

        private static final NONE = new None<Object>()

        @Override
        Iterator<T> iterator() {
            Collections.emptySet().iterator()
        }

        def <E> None<E> map(Closure<E> c) {
            Option.none()
        }

        def <F, E> None<F> rightShiftUnsigned(Option<E> other, Closure<F> combine) {
            Option.none()
        }

        boolean isNone() {
            return true
        }

        boolean isSome() {
            return false
        }

        Option flatten(){
            return this
        }

        Option<T> ifNoneThanSome(Closure<T> c){
            some(c())
        }

        String toString(){
            "none"
        }


    }

    static class Some<T> extends Option<T> {
        final T value;

        public Some(value) {
            this.value = value
        }

        boolean isNone() {
            return false
        }

        boolean isSome() {
            return true
        }

        @Override
        Iterator<T> iterator() {
            Collections.singleton(value).iterator()
        }

        def <E> Option<E> map(Closure<E> c) {
            return new Some<E>(c(value))
        }

        def <F, E> Option<F> rightShiftUnsigned(Option<E> other, Closure<F> combine) {
            if (other.isNone()) return none()

            new Some<F>(bind(value, other.value))
        }

        Option flatten(){
            if(value instanceof Option){
                return ((Option)value).flatten()
            }else{
                return this
            }
        }

        Option<T> ifNoneThanSome(Closure<T> c){
            this
        }

        String toString(){
            "some($value)"
        }

    }
}
