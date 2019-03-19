package ru.ak.info;

import ru.ak.ldap.LdapService;

import javax.jws.WebMethod;
import javax.jws.WebService;

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
        return version_1_0_0_7();
    }

    private String version_1_0_0_1() {
        // Создание проекта
        return "1.0.0.1";
    }

    private String version_1_0_0_2() {
        // Реализовано чтение GECOS
        return "1.0.0.2";
    }

    private String version_1_0_0_3() {
        // Реализовано чтение thumbnailPhoto
        return "1.0.0.3";
    }

    private String version_1_0_0_4() {
        // Чтение атрибута objectGUID
        return "1.0.0.4";
    }

    private String version_1_0_0_5() {
        // Добавление чтения единиц с указанием baseDN, а также указанных атрибутов
        // Добавление метода для получения всех возможных атрибутов
        return "1.0.0.5";
    }

    private String version_1_0_0_6() {
        /* Добавление чтение атрибута Description, который при наличии кириллицы
         приходит в base64 */
        return "1.0.0.6";
    }

    private String version_1_0_0_7() {
        /* Добавление чтения атрибута Title, который при наличии кириллицы
        приходит в base64 */
        return "1.0.0.7";
    }
}
