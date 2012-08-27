/**
 * Created with IntelliJ IDEA.
 * User: okrammer
 * Date: 27/08/2012
 * Time: 08:21
 * To change this template use File | Settings | File Templates.
 */
class TColor {

    static String redFg(String s) {
        "\033[31m$s\033[0m"
    }
    static String greenFg(String s) {
        "\033[32m$s\033[0m"
    }
    static String yellowBg(String s) {
        "\033[43m$s\033[0m"
    }


}
