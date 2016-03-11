open class A<T>()
class G<T>()


class B : A<<error descr="[WRONG_NUMBER_OF_TYPE_ARGUMENTS_FOR_CLASS] Type argument expected for G">G</error>>()