# Diagram Block Generator
<i>Diagram Block Generator</i> - лёгкая в использовании программа позволяющая создавать блок схемы и сохранять их в различных форматах (.png, .tex, .svg).
Экспорт в .jpg находится в разработке.
# Установка
## Windows
* Вы можете скачать последнюю версию программы в разделе Releases

# Использование
* <b>Навигация по блок схеме.</b>
    * <b>Выбор элемента. </b>Наведитесь курсором мыши на желаемый элемент. Элемент, который будет выбран при нажатии ЛКМ будет подсвечен. Выбрать элемент можно при помощи нажатия ЛКМ - в этом случае он поменяет свою окраску на более светлую. Выбрать можно не все элементы. Нельзя выбрать служебные блоки "Контейнер" и "Добавить"
    * <b>Перемещение по холсту. </b>Осуществляется при помощи перемещения мыши с зажатой ПКМ
    * <b>Размер. </b>Изменяется при помощи колёсика мыши
* <b>Добавление элемента в блок схему.</b> Перетащите блок из вкладки "Элементы" в поле блок схемы. Если Вы наведёте выбранный элемент на уже существующий, его тип будет изменён на выбранный. При этом если новый тип будет содержать меньше данных (пример: Модификатор -> Процесс), появится сообщение с предупреждением. Также Вы можете добавить элемент после и до элемента. Для того, чтобы добавить новый элемент до какого-либо элемента, переместите выбранный элемент выше желаемого. Для того, чтобы разместить элемент после - ниже желаемого.
* <b>Удаление элемента блок схемы.</b> Удалить элемент из блок схемы можно несколькими способами.
  * Выбрав элемент и нажав клавишу `DELETE`
  * Выбрав элемент и нажав в меню пункт "Изменить" -> "Удалить"
* <b>Изменение элемента.</b> Изменение свойств элемента находится в правой секции "Свойства элемента". Чтобы изменить элемент, выберите его. После этого в указанной секции появятся его свойства. Вы можете изменять следующие свойства элементов:
  * Терминатор, Данные, Процесс, Предопр. процесс, Цикл с нефиксированным количеством повторений, Цикл с фиксированным количеством повторений: текст
  * Решение: текст, расположение негативной и позитивной ветки
* <b>Создание нового файла.</b> Создаёт пустой проект. Может быть вызван двумя способами
  * При выборе пункта меню "Файл" -> "Новый"
  * При нажатии сочетания клавиш `Ctrl + N`
* <b>Открытие файла.</b> Открывает проект (файл с расширением .dbg). Может быть вызван двумя способами
    * При выборе пункта меню "Файл" -> "Открыть"
    * При нажатии сочетания клавиш `Ctrl + O`
* <b>Сохранить.</b> Сохраняет проект в файл, если связан с ним, иначе ведёт себя как "Сохранить как". Может быть вызван двумя способами
    * При выборе пункта меню "Файл" -> "Сохранить"
    * При нажатии сочетания клавиш `Ctrl + S`
* <b>Сохранить как.</b> Вызывает окно сохранения файла, после чего сохраняет в выбранный файл проект. Может быть вызван двумя способами
    * При выборе пункта меню "Файл" -> "Сохранить как"
    * При нажатии сочетания клавиш `Ctrl + Shift + S`
* <b>Экспорт.</b> вызывает окно экспортирования проекта. Может быть вызван двумя способами
    * При выборе пункта меню "Файл" -> "Экспорт"
    * При нажатии сочетания клавиш `Ctrl + E`
* <b>Окно экспорта.</b> позволяет экспортировать проект в один из доступных форматов (.png, .tex, .svg). Экспортированные файлы не содержат сетку и блоки "Добавить". Содержит следующие настройки
  * <b>Экспортировать как.</b> Путь и формат в котором будет сохранён файл.
  * <b>Ширина и единицы измерения.</b> Ширина в единицах измерения (пикс., дм., см.)
  * <b>Высота.</b> Высота в единицах измерения. Отношение сторон всегда фиксированно и зависит от отношения исходного проекта
  * <b>(В РАЗРАБОТКЕ) Плотность и единицы измерения.</b> Показывает отношение пикселей на дюйм или сантиметры.
  * <b>(НОВОЕ) Стиль.</b> Позволяет выбрать оформление экспортированной блок схемы.
* <b>(НОВОЕ) Выбрать стиль. </b>Позволяет выбрать стиль блок схемы по умолчанию. Можно вызвать при помощи выбора пункта меню "Файл" -> "Выбрать стиль"
* <b>Выход. </b>Прекращает выполнение программы. Вызывается через пунки меню "Файл" -> "Выход".
* <b>Отменить.</b> Отменяет последнее действие. Может быть вызван двумя способами
    * При выборе пункта меню "Изменить" -> "Отменить"
    * При нажатии сочетания клавиш `Ctrl + Z`
* <b>Повторить.</b> Возвращает отменённое действие. Может быть вызван двумя способами
  * При выборе пункта меню "Изменить" -> "Повторить"
  * При нажатии сочетания клавиш `Ctrl + Y`
* <b>(НОВОЕ) Окно выбора стиля.</b> Позволяет изменять шрифт, оформление, размеры и прочие свойства блок схемы. 
  * <b>Имя стиля.</b> Имя, которое будет присвоено новому стилю при клонировании. Если поле пустое, имя нового стиля - имя клонируемого стиля.
  * <b>Клонировать.</b> Клонирует выбраннуй стиль, все значения копируются из старого стиля в новый без изменений.
  * <b>Выпадающий список со стилями.</b> Содержит все стили. По умолчанию содержит readonly стиль по умолчанию Default. Остальные стили можно изменять.
  * <b>Импорт.</b> Позволяет импортировать тему из файла. Действует аналогично клонированию, однако значения копируются не из выбранной темы, а из файла.
  * <b>Экспорт.</b> Экспортирует текущий стиль в файл, который в дальнейшем может быть использован для импорта стиля.
  * <b>Окно просмотра блок схемы.</b> Позволяет просмотреть вид блок схемы в выбранном стиле. Навигация по блок схеме сохраняется и осуществляется при помощи прокручивания колёсика мыши, передвижения мыши с зажатой ПКМ, нажатием ЛКМ по элементу для его выбора. Блок схема находится в режиме readonly.
    * <b>Режим редактирования.</b> Включает режим viewport, позволяющий просмотреть оформление блок схемы в режиме редактирования, если отмечен. Иначе - вид блок схемы при экспорте.
    * <b>Режим перетаскивания.</b> Эмулирует режим перетаскивания элемента, если отмечен.
    * <b>Загрузить.</b> Загружает в окно просмотра блок схемы блок схему из .dbg файла.
    * <b>Сбросить.</b> Загружает в окно просмотра блок схемы блок схему по умолчанию. 
  * <b>Окно редактирования стиля.</b> Позволяет изменять значения выбранного стиля.
