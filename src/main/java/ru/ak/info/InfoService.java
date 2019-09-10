package ru.ak.info;

import ru.ak.ldap.LdapService;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

/**
 * Корневой Web-сервис, содержащий метод получения версии
 * @author akakushin
 */
@WebService(name = "Info", serviceName = "Info", portName = "InfoPort") 
public class InfoService extends LdapService {

    /**
     * Получение версии компоненты
     * @return Версия компоненты 
     */
    @WebMethod(operationName = "version")
    public String version() {
        return builds().size() == 0 ? "null" : builds().get(builds().size() - 1).getVersion();
    }

    @WebMethod(operationName = "details") 
    public Builds details() {
        return new Builds(builds());
    }

    public static class Builds {
        private List<Build> builds;

        public Builds(List<Build> builds) {
            this.builds = builds;
        }

        @XmlElement
        public List<Build> getBuilds() {
            return builds;
        }
    }
    
    private List<Build> builds() {
        List<Build> builds = new ArrayList<>();
        builds.add(            
            new Build("1.0.0.1", description_1_0_0_1()));

        builds.add(            
            new Build("1.0.0.2", description_1_0_0_2()));

        builds.add(            
            new Build("1.0.0.3", description_1_0_0_3()));

        builds.add(            
            new Build("1.0.0.4", description_1_0_0_4()));

        builds.add(            
            new Build("1.0.0.5", description_1_0_0_5()));

        builds.add(            
            new Build("1.0.0.6", description_1_0_0_6()));

        builds.add(            
            new Build("1.0.0.7", description_1_0_0_7()));
        
        builds.add(            
            new Build("1.0.0.8", description_1_0_0_8()));

        builds.add(            
            new Build("1.0.0.9", description_1_0_0_9()));

        builds.add(            
            new Build("1.0.0.10", description_1_0_0_10()));
                        
        return builds;
    }

    private String description_1_0_0_1() {
        return "Создание проекта";
    }

    private String description_1_0_0_2() {
        return "Реализовано чтение GECOS";
    }

    private String description_1_0_0_3() {
        return "Реализовано чтение thumbnailPhoto";
    }

    private String description_1_0_0_4() {
        return "Чтение атрибута objectGUID";
    }

    private String description_1_0_0_5() {
        return 
            "Добавление чтения единиц с указанием baseDN, а также указанных атрибутов"
            + "\nДобавление метода для получения всех возможных атрибутов";        
    }

    private String description_1_0_0_6() {
        return "Добавление чтение атрибута Description, который при наличии кириллицы приходит в base64";
    }

    private String description_1_0_0_7() {
        return "Добавление чтения атрибута Title, который при наличии кириллицы приходит в base64";
    }

    private String description_1_0_0_8() {
        return "Удаление пробелов и непечатаемых символов из строки, содержащей список атрибутов";
    }

    private String description_1_0_0_9() {
        return "Добавлено чтение атрибута manager";
    }

    private String description_1_0_0_10() {
        return "Добавлено чтение массивов, например memberOf";
    }
}