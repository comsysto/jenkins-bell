// monitor configure

import groovy.transform.Field
import static Option.*

@Field File configFolder = new File(new File(System.getProperty("user.home")), ".jenkins-bell")

Option<File> configFile(name){
    file(configFolder, name)
}

Option<File> configFolder(){
    some(configFolder)
}


Option<File> folder(File parent, String name) {
    assert name, "Illegal file name not set!"
    def f = new File(parent, name)
    assert f.exists() || f.mkdirs(), "Failed making directory '$f'"
    some(f)
}

Option<File> file(File parent, String name) {
    assert name, "Illegal file name not set!"
    def f = new File(parent, name)
    assert f.parentFile.exists() || f.parentFile.mkdirs(), "Failed making directory '$f.parentFile'"
    assert f.exists()  || f.createNewFile(), "Failed making file '$f'"
   some(f)
}
