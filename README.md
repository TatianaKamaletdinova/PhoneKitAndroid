# PhoneKitAndroid
phone_kit - это форматер и ui-компонент для форматирования и ввода номера телефона всех стран мира

https://user-images.githubusercontent.com/23185210/204284062-23e8230a-d76f-4a00-b619-1f49912f51fd.mp4

![image](https://user-images.githubusercontent.com/23185210/204283813-94c2513f-ed87-484f-b103-e46998330866.png)
![image](https://user-images.githubusercontent.com/23185210/204283860-bbcbc9f8-6b46-4b07-99c3-de2862a44204.png)

# Демонстрация
[Sample](https://github.com/TatyanaBazhanova/PhoneKitAndroid/tree/master/app) приложение демонстрирует, насколько просто на самом деле использовать библиотеку.

[Unit-tests](https://github.com/TatyanaBazhanova/PhoneKitAndroid/tree/master/phone_kit/src/test/java/ru/example/country_phone_kit) unit-тесты на форматор 


# Использование
 
В xml:



```kotlin
  <ru.kamal.phone_kit.api.phone_view.PhoneView
            android:id="@+id/phoneNumberView2"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            //Устанавливает дефолтную страну - Россию
            app:initDefaultCountry="true"
            app:layout_constraintStart_toStartOf="parent" />
```
Настроить на Activity/Fragment

```kotlin
 with(binding.phoneNumberView) {
            //Устанавливает фокус и открывает клавиатуру
            setupFocusAndShowKeyboard(true)
            //Флоу для получения результата корректного ввода телефона
            phoneFlow
                .onEach { validatePhone(it) }
                .launchIn(scope)          
            //Устанавливает номер в поле
            setupPhone("375777")
        }
```

### Форматер номера телефона с поддержкой кодов всех стран мира
 
```kotlin
private val phoneFormatter: PhoneFormatter = getPhoneFormatter(resources) 

//метод, определяет по номеру телефона его код страны и форматирует номер по маске этой страны
val formattedPhone = phoneFormatter.format("79652346179")
//+7 965 234-61-79
formattedPhone

//Возвращает инфо о стране по номеру
val countryByNumber = phoneFormatter.getCountryByNumber("79652346179")
//Country(name=Россия, code=7, isoCode=643, shortname=RU, phoneFormat=000 000-00-00, maxPhoneLength=10)
countryByNumber

//Возвращает телефон как цифры, без суффикса "+" и кода страны
val digitNumber = phoneFormatter.getClearText("+7 965 234-61-79")
//9652346179
digitNumber

//Форматирует номер и подставляет звездочки после кода оператора телефона 
//или если нет кода оператора подставляет звездочки на последние 1 или 2 цифры номера
val maskedNumber = phoneFormatter.formatWithSecretMask("79652346179")
//+7 965 ***-61-79
maskedNumber

```
