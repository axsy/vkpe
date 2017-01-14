vkpe (в.к.п.е)
==============

**RU:**

Утилита экспорта фотографий из ВКонтакте
========================================

Приложение экспортирует фотографии из социальной сети ВКонтакте. Оно использует открытый протокол авторизации OAuth 2, пользователи не передают приложению свои имя пользователя и пароль и, таким образом, доступ к аккаунту не может быть скомпроментирован.

Приложение экспортирует фотографии в максимальном доступном разрешении из всех альбомов пользователя, включая служебные альбомы: фотографии на стене, фотографии профиля и сохраненные фотографии. Все экспортированные фотографии будут иметь файловые атрибуты времени согласно данных ВКонтакте.

Подготовка
----------

Приложение авторизует себя в сети ВКонтакте как веб-приложение (несмотря на то, что оно является обыкновенным консольным приложением). Поэтому первое, что необходимо сделать, это создать новое приложение ВКонтакте:

 1. Откройте [страницу создания приложения ВКонтакте](https://vk.com/editapp?act=create) и выберите платформу **Веб-сайт**. Введите следующие данные в поля формы:

  * Название: (любое название, например `vkpe`)
  * Адрес сайта: `http://localhost`
  * Базовый домен: `localhost`

 2. Подтвердите приложение (следуйте инструкциям на экране).
 3. Перейдите на вкладку **Настройки** и убедитесь, что Состояние - **Приложение включено и видно всем**. Скопируйте и сохраните где-нибудь значения полей **ID приложения** и **Защищенный ключ**. Впоследствии эти данные будут переданы самому консольному приложению.

Экспорт фотографий
------------------

### Требования

**в.к.п.е** является Java приложением и использует некоторые сторонние библиотеки в процессе своей работы, которые контролируются Maven - инструментом управления проектом. Таким образом, для того, чтобы запустить приложение, необходимо установить:

* [Среду времени выполнения Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html), которая является минимальной поддерживаемой версией Java, требуемой для запуска приложения.
* [Maven 3](https://maven.apache.org/install.html) является минимальной требуемой версией инструмента управления проектом.

На самом деле Maven необходим только для того, чтоб осуществить сборку приложения. Собранное приложение может быть запущено без Maven, установленного в системе. Конечно, для установки этих зависимостей желательно (и наиболее предпочительно) использование инструмента управления пакетами вашей ОС (например `apt` в дистрибутивах, основаных на Debian или `brew` на MacOS).

### Запуск приложения

Приложению требуется три параметра для его запуска, это ID приложения и защищенный ключ, который вы сохранили во время создания приложения ВКонтакте, а так же путь к директории, в которой необходимо сохранить экспортированные фотографии. Существует два способа запуска приложения:

#### Первый способ

Используйте цель Maven `exec:java`. Данная команда непосредственно загружает все необходимые зависимости и запускает приложение, запустите ее из корневой директории приложения:

```
mvn exec:java -Dexec.args="-appId=<ID приложения> -secureKey=<Защищенный ключ> -path=<Путь к папке>"
```

#### Второй способ

Соберите приложение и запустите переносимый jar файл приложения. С помощью этого способа вы получаете собранное переносимое приложение, способное к переносу и запуску на системах, где единственным требованием является наличие Java 8. Выполните сборку приложения из его корневой директории:

```
mvn clean install
```

Затем зайдите в директорию `target/vkpe`, здесь находится собранное приложение: исполняемый Java архив и все сторонние библиотеки в папке `lib`. Запустите приложение и передйте ему все требуемые параметры:

```
java -jar vkpe.jar -appId=<Application ID> -secureKey=<Secure key> -path=<Path>
```

#### Авторизация

После того, как приложение было запущено, оно откроет в браузере системы по умолчанию страницу, где ВКонтакте предложит вам аутентифицироваться и запросит подтверждение прав приложения на доступ к вашим фотографиям. После того как вы подтвердите права приложения вы сможете наблюдать за процессом экспортирования фотографий с помощью сообщений приложения в консоли. В случае, если ВКонтакте запросит подтверждение капчи, приложение снова откроет браузер системы по умолчанию с предложением ввести текст с отображаемой капчи.

**EN:**

Vkontakte Photo Export Tool
===========================

This application exports photo media content from social network VKontakte. It uses OAuth 2 open authorization protocol, users do not send their login and password so accounts can not be compromised.

Application exports photos in maximum available resolution from all the albums of user including service albums: photos on wall, profile photos and saved photos. All the exported files will have an assigned timestamp file attributes according to the VKontakte data.

Preparation
-----------

This application authorizes itself as a website VKontakte application (despite the fact it is ordinary standalone console application). So first you have to do is to create a new VKontakte application:

 1. Open [VKontakte application creation page](https://vk.com/editapp?act=create) and choose **Website** platform. Enter the following values into corresponding fields of the form and click **Connect Site** button:

  * Title: (whatever title you want, i.e. `vkpe`)
  * Site address: `http://localhost`
  * Base domain: `localhost`

 2. Confirm application (follow instructions on page)
 3. Go to **Settings** tab and make sure that Application status is **Application on and visible to all**. Copy and store somewhere values of **Application ID** and **Secure key**. This data will be provided to the tool later.

Export photos
-------------

### Requirements

**vkpe** is Java application and use some external dependencies that are managed by Maven project management tool. So in order to run an application you have to install them both:

  * [Java 8 Runtime Environment](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) is the minimum required version of Java to run the application.
  * [Maven 3](https://maven.apache.org/install.html) is the minimum required version of project management tool.

In fact Maven is necessary to build the project only. An assembled application distribution can be executed without Maven installed in the system. Of course, in order to install these dependencies it is recommended (and much preferable) to use package management tool provided by your OS (i.e. `apt` in Debain-based systems or `brew` on MacOS).

### Running application

Application requires three parameters, they are application id and secret key you stored after you've created VKontakte application and path for the photos to be exported to. There are two ways to run the application:

#### First way

Use `exec:java` goal of Maven. This command downloads all the necessary dependencies and runs an application directly, run it within application directory:

```
mvn exec:java -Dexec.args="-appId=<Application ID> -secureKey=<Secure key> -path=<Path>"
```

#### Second way

Build an application and run the distributable jar file. This way you'll get the distribution that can be transfered to other machine where Java 8 is the only requirement. Build an application from the application directory:

```
mvn clean install
```

Then enter the `target/vkpe` directory. It keeps the assembled application: it's executable java archive and all the dependencies in `lib` directory. Run the application and pass all the required parameters:

```
java -jar vkpe.jar -appId=<Application ID> -secureKey=<Secure key> -path=<Path>
```

#### Authorization

After the application is executed it will open default web browser with page where VKontakte will ask you to authenticate and will request you to grant an access to your photos to the application. After you confirmed the access grant you'll be able to watch the export process via application logs in console. In case VK requested captcha confirmation default browser will be opened again with the request to enter the text from the captcha.
