# GitHubUsersGraber

Screencast:  https://youtu.be/3jzKN7-vyj8

Firebase console: https://drive.google.com/file/d/1MIqQIpA6a3KJi8jKPqJ9DcczoFWdro5Z/view?usp=sharing



завдання анрдоід


⁃витягнути юзерів в список recyclerview https://api.github.com/users
⁃при тапі на одного юзера показати його репозиторії https://api.github.com/users/{login}/repos <https://api.github.com/users/%7Blogin%7D/repos>
⁃всі дані повинні кешитись в realm
⁃підключити FCM і зробити кастомний пуш з полем data в якому буде userId (id користувача) і changesCount -  поле яке тре показати в списку користувачів в червоному кружку. Відкриваєм апп бачим список користувачів гітхаба, якщо приходить пуш з полем дата, тре його розпарсити, і внести зміни в реалм обєкт користувача, змінити поле changesCount, і апдейтнути список
⁃якщо поле changesCount == 0 то не показувати нічого


все зробити на java/kotlin(бажано) , повинно відкривати спочатку закешоване потім вже апдейтити з інтернета, якщо список пустий і йде заванатження тре показати лоадер


