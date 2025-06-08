package com.realestateassistant.pro.data.service

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.realestateassistant.pro.R
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.model.PropertyCharacteristicsConfig
import com.realestateassistant.pro.domain.model.PropertyTypeCharacteristics
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

/**
 * Сервис для отрисовки содержимого PDF файлов.
 * 
 * Отвечает за формирование визуального представления данных объекта недвижимости в PDF.
 * Создает структурированный PDF-документ с разделами, соответствующими экрану детальной информации.
 */
@Singleton
class PdfExportService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val resources: Resources = context.resources
    
    // Размеры страницы A4 в пикселях при 72dpi
    private val pageWidth = 595  // 8.26 дюймов
    private val pageHeight = 842 // 11.69 дюймов
    
    // Отступы страницы и стандартные размеры элементов
    private val pageMargin = 50
    private val contentWidth = pageWidth - (pageMargin * 2)
    private val headerHeight = 70
    private val footerHeight = 50
    private val sectionSpacing = 25
    private val sectionHeaderHeight = 40
    
    // Цветовая схема
    private val primaryColor = Color.rgb(33, 150, 243) // Синий
    private val textColor = Color.rgb(33, 33, 33) // Почти черный
    private val secondaryTextColor = Color.rgb(117, 117, 117) // Серый
    private val sectionHeaderColor = Color.rgb(25, 118, 210) // Темно-синий
    private val backgroundColor = Color.rgb(245, 245, 245) // Светло-серый
    private val dividerColor = Color.rgb(224, 224, 224) // Светло-серый для разделителей
    
    // Шрифты
    private val titlePaint = TextPaint().apply {
        color = primaryColor
        textSize = 24f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }
    
    private val subtitlePaint = TextPaint().apply {
        color = primaryColor
        textSize = 18f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }
    
    private val regularTextPaint = TextPaint().apply {
        color = textColor
        textSize = 12f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        isAntiAlias = true
    }
    
    private val smallTextPaint = TextPaint().apply {
        color = secondaryTextColor
        textSize = 10f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        isAntiAlias = true
    }
    
    private val boldTextPaint = TextPaint().apply {
        color = textColor
        textSize = 12f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }
    
    private val highlightTextPaint = TextPaint().apply {
        color = primaryColor
        textSize = 16f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }
    
    private val sectionHeaderPaint = TextPaint().apply {
        color = Color.WHITE
        textSize = 16f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }
    
    private val sectionHeaderBgPaint = Paint().apply {
        color = sectionHeaderColor
        style = Paint.Style.FILL
    }
    
    private val dividerPaint = Paint().apply {
        color = dividerColor
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }
    
    private val backgroundPaint = Paint().apply {
        color = backgroundColor
        style = Paint.Style.FILL
    }
    
    private val labelPaint = TextPaint().apply {
        color = secondaryTextColor
        textSize = 12f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        isAntiAlias = true
    }
    
    private val valuePaint = TextPaint().apply {
        color = textColor
        textSize = 12f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        isAntiAlias = true
    }
    
    // Форматтер цен
    private val priceFormatter = NumberFormat.getNumberInstance(Locale("ru", "RU")).apply {
        maximumFractionDigits = 0
    }
    
    /**
     * Рисует содержимое PDF документа для объекта недвижимости
     */
    fun drawPropertyContent(document: PdfDocument, property: Property, images: List<Bitmap>) {
        // Получаем текущую дату
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        
        // Получаем безопасное название улицы (без номера дома)
        val streetName = extractStreetName(property.address)
        
        // Создаем заголовок документа
        val title = when {
            property.isStudio -> "Студия на ${streetName}"
            else -> "${property.roomsCount}-комнатная ${property.propertyType.lowercase()} на ${streetName}"
        }
        
        // Счетчик страниц
        var pageNumber = 1
        
        // Создаем первую страницу
        var currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = document.startPage(currentPageInfo)
        var canvas = page.canvas
        
        // Начальная позиция для рисования
        var yPosition = 0f
        
        // Рисуем шапку документа
        yPosition = drawHeader(canvas, title, currentDate, yPosition, pageNumber)
        
        // Отступ после шапки
        yPosition += 20f
        
        // Рисуем раздел с основной информацией
        val mainInfoHeight = calculateMainInfoHeight(property)
        if (yPosition + mainInfoHeight > pageHeight - footerHeight) {
            // Не хватает места, создаем новую страницу
            drawFooter(canvas, pageHeight - footerHeight.toFloat(), pageNumber)
            document.finishPage(page)
            
            pageNumber++
            currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(currentPageInfo)
            canvas = page.canvas
            
            yPosition = drawHeader(canvas, title, currentDate, 0f, pageNumber)
            yPosition += 20f
        }
        
        yPosition = drawMainInfo(canvas, property, yPosition)
        yPosition += sectionSpacing
        
        // Рисуем раздел с фотографиями
        if (images.isNotEmpty()) {
            val photosHeight = calculatePhotosHeight(images)
            if (yPosition + photosHeight > pageHeight - footerHeight) {
                // Не хватает места, создаем новую страницу
                drawFooter(canvas, pageHeight - footerHeight.toFloat(), pageNumber)
                document.finishPage(page)
                
                pageNumber++
                currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(currentPageInfo)
                canvas = page.canvas
                
                yPosition = drawHeader(canvas, title, currentDate, 0f, pageNumber)
                yPosition += 20f
            }
            
            yPosition = drawPhotosSection(canvas, images, yPosition)
            yPosition += sectionSpacing
        }
        
        // Рисуем раздел с ценой и условиями
        val priceInfoHeight = calculatePriceInfoHeight(property)
        if (yPosition + priceInfoHeight > pageHeight - footerHeight) {
            // Не хватает места, создаем новую страницу
            drawFooter(canvas, pageHeight - footerHeight.toFloat(), pageNumber)
            document.finishPage(page)
            
            pageNumber++
            currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(currentPageInfo)
            canvas = page.canvas
            
            yPosition = drawHeader(canvas, title, currentDate, 0f, pageNumber)
            yPosition += 20f
        }
        
        yPosition = drawPriceInfo(canvas, property, yPosition)
        yPosition += sectionSpacing
        
        // Рисуем раздел с адресом
        val addressHeight = calculateAddressHeight(property)
        if (yPosition + addressHeight > pageHeight - footerHeight) {
            // Не хватает места, создаем новую страницу
            drawFooter(canvas, pageHeight - footerHeight.toFloat(), pageNumber)
            document.finishPage(page)
            
            pageNumber++
            currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(currentPageInfo)
            canvas = page.canvas
            
            yPosition = drawHeader(canvas, title, currentDate, 0f, pageNumber)
            yPosition += 20f
        }
        
        yPosition = drawAddressSection(canvas, property, yPosition)
        yPosition += sectionSpacing
        
        // Рисуем раздел с характеристиками
        val featuresHeight = calculateFeaturesHeight(property)
        if (yPosition + featuresHeight > pageHeight - footerHeight) {
            // Не хватает места, создаем новую страницу
            drawFooter(canvas, pageHeight - footerHeight.toFloat(), pageNumber)
            document.finishPage(page)
            
            pageNumber++
            currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(currentPageInfo)
            canvas = page.canvas
            
            yPosition = drawHeader(canvas, title, currentDate, 0f, pageNumber)
            yPosition += 20f
        }
        
        yPosition = drawFeaturesSection(canvas, property, yPosition)
        yPosition += sectionSpacing
        
        // Рисуем раздел с описанием
        if (!property.description.isNullOrBlank()) {
            val descriptionHeight = calculateDescriptionHeight(property)
            if (yPosition + descriptionHeight > pageHeight - footerHeight) {
                // Не хватает места, создаем новую страницу
                drawFooter(canvas, pageHeight - footerHeight.toFloat(), pageNumber)
                document.finishPage(page)
                
                pageNumber++
                currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(currentPageInfo)
                canvas = page.canvas
                
                yPosition = drawHeader(canvas, title, currentDate, 0f, pageNumber)
                yPosition += 20f
            }
            
            yPosition = drawDescriptionSection(canvas, property, yPosition)
            yPosition += sectionSpacing
        }
        
        // Рисуем раздел с удобствами и особенностями
        if (property.amenities.isNotEmpty() || property.nearbyObjects.isNotEmpty() || property.views.isNotEmpty()) {
            val amenitiesHeight = calculateAmenitiesHeight(property)
            if (yPosition + amenitiesHeight > pageHeight - footerHeight) {
                // Не хватает места, создаем новую страницу
                drawFooter(canvas, pageHeight - footerHeight.toFloat(), pageNumber)
                document.finishPage(page)
                
                pageNumber++
                currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(currentPageInfo)
                canvas = page.canvas
                
                yPosition = drawHeader(canvas, title, currentDate, 0f, pageNumber)
                yPosition += 20f
            }
            
            yPosition = drawAmenitiesSection(canvas, property, yPosition)
            yPosition += sectionSpacing
        }
        
        // Рисуем подвал документа
        drawFooter(canvas, pageHeight - footerHeight.toFloat(), pageNumber)
        
        // Завершаем последнюю страницу
        document.finishPage(page)
    }
    
    /**
     * Рисует шапку PDF документа с логотипом и номером страницы
     */
    private fun drawHeader(canvas: Canvas, title: String, date: String, startY: Float, pageNumber: Int): Float {
        // Рисуем фон шапки
        val headerRect = Rect(0, startY.toInt(), pageWidth, startY.toInt() + headerHeight)
        canvas.drawRect(headerRect, backgroundPaint)
        
        // Позиция для контента
        var yPosition = startY + 20f
        
        // Рисуем название приложения (логотип)
        val appName = resources.getString(R.string.app_name)
        canvas.drawText(appName, pageMargin.toFloat(), yPosition + 15f, titlePaint)
        
        // Рисуем номер страницы справа
        val pageText = "Страница $pageNumber"
        val pageTextWidth = smallTextPaint.measureText(pageText)
        canvas.drawText(
            pageText,
            pageWidth - pageMargin - pageTextWidth,
            yPosition + 15f,
            smallTextPaint
        )
        
        // Рисуем дату документа справа
        val dateText = "Дата: $date"
        val dateTextWidth = smallTextPaint.measureText(dateText)
        canvas.drawText(
            dateText,
            pageWidth - pageMargin - dateTextWidth,
            yPosition + 30f,
            smallTextPaint
        )
        
        yPosition += headerHeight - 20f
        
        // Рисуем заголовок документа
        canvas.drawText(title, pageMargin.toFloat(), yPosition, subtitlePaint)
        
        return yPosition
    }
    
    /**
     * Рисует подвал документа
     */
    private fun drawFooter(canvas: Canvas, yPosition: Float, pageNumber: Int) {
        // Рисуем фон подвала
        val footerRect = Rect(0, yPosition.toInt(), pageWidth, pageHeight)
        canvas.drawRect(footerRect, backgroundPaint)
        
        // Рисуем горизонтальную линию
        canvas.drawLine(
            pageMargin.toFloat(),
            yPosition + 5f,
            pageWidth - pageMargin.toFloat(),
            yPosition + 5f,
            dividerPaint
        )
        
        // Текст подвала
        val footerText = "© ${resources.getString(R.string.app_name)} — экспорт данных объекта"
        
        canvas.drawText(
            footerText,
            pageMargin.toFloat(),
            yPosition + 25f,
            smallTextPaint
        )
        
        // Номер страницы
        val pageText = "Стр. $pageNumber"
        val pageTextWidth = smallTextPaint.measureText(pageText)
        
        canvas.drawText(
            pageText,
            pageWidth - pageMargin - pageTextWidth,
            yPosition + 25f,
            smallTextPaint
        )
    }
    
    /**
     * Рисует основную информацию об объекте
     */
    private fun drawMainInfo(canvas: Canvas, property: Property, startY: Float): Float {
        var yPosition = startY
        
        // Рисуем заголовок раздела
        yPosition = drawSectionHeader(canvas, "Общая информация", yPosition)
        
        // Рисуем блок с основной информацией
        val boxRect = Rect(
            pageMargin,
            yPosition.toInt(),
            pageWidth - pageMargin,
            yPosition.toInt() + 120 // Фиксированная высота для основной информации
        )
        canvas.drawRect(boxRect, backgroundPaint)
        
        yPosition += 20f // Отступ внутри блока
        
        // Левая колонка
        var leftColumnY = yPosition
        val leftColumnX = pageMargin + 20f
        
        // Тип недвижимости и комнаты
        val propertyTypeText = if (property.isStudio) {
            "Студия"
        } else {
            "${property.roomsCount}-комн. ${property.propertyType}"
        }
        canvas.drawText(propertyTypeText, leftColumnX, leftColumnY, highlightTextPaint)
        leftColumnY += 25f
        
        // Площадь
        canvas.drawText("Площадь: ${property.area} м²", leftColumnX, leftColumnY, regularTextPaint)
        leftColumnY += 20f
        
        // Этаж
        if (property.floor > 0) {
            val floorText = if (property.totalFloors > 0) {
                "Этаж: ${property.floor} из ${property.totalFloors}"
            } else {
                "Этаж: ${property.floor}"
            }
            canvas.drawText(floorText, leftColumnX, leftColumnY, regularTextPaint)
        }
        
        // Правая колонка
        var rightColumnY = yPosition
        val rightColumnX = (pageWidth / 2f) + 20f
        
        // Цена
        val priceText = when {
            property.monthlyRent != null && property.monthlyRent > 0 -> {
                "${formatPrice(property.monthlyRent)} ₽/мес."
            }
            property.dailyPrice != null && property.dailyPrice > 0 -> {
                "${formatPrice(property.dailyPrice)} ₽/сутки"
            }
            else -> {
                "По запросу"
            }
        }
        canvas.drawText("Цена: $priceText", rightColumnX, rightColumnY, highlightTextPaint)
        rightColumnY += 25f
        
        // Тип ремонта
        if (property.repairState.isNotEmpty()) {
            canvas.drawText("Ремонт: ${property.repairState}", rightColumnX, rightColumnY, regularTextPaint)
            rightColumnY += 20f
        }
        
        // Мебель и техника
        val furnishingText = when {
            !property.noFurniture && property.hasAppliances -> "С мебелью и техникой"
            !property.noFurniture -> "С мебелью"
            property.hasAppliances -> "С техникой"
            else -> "Без мебели и техники"
        }
        canvas.drawText(furnishingText, rightColumnX, rightColumnY, regularTextPaint)
        
        return yPosition + 120f
    }
    
    /**
     * Рисует заголовок раздела
     */
    private fun drawSectionHeader(canvas: Canvas, title: String, startY: Float): Float {
        // Рисуем фон заголовка
        val headerRect = Rect(
            pageMargin,
            startY.toInt(),
            pageWidth - pageMargin,
            startY.toInt() + sectionHeaderHeight
        )
        canvas.drawRect(headerRect, sectionHeaderBgPaint)
        
        // Рисуем текст заголовка
        val textY = startY + (sectionHeaderHeight / 2) + (sectionHeaderPaint.textSize / 3)
        canvas.drawText(title, pageMargin + 20f, textY, sectionHeaderPaint)
        
        return startY + sectionHeaderHeight
    }
    
    /**
     * Рассчитывает высоту блока с основной информацией
     */
    private fun calculateMainInfoHeight(property: Property): Float {
        // Заголовок раздела + фиксированная высота блока
        return sectionHeaderHeight + 120f
    }
    
    /**
     * Рисует компактную галерею изображений объекта
     */
    private fun drawImagesGallery(canvas: Canvas, images: List<Bitmap>, startY: Float): Float {
        if (images.isEmpty()) return startY
        
        var yPosition = startY
        
        // Определяем размеры для изображений
        val availableWidth = pageWidth - (pageMargin * 2)
        val maxHeight = 150 // Максимальная высота для изображения
        
        // Отображаем все изображения, но не более 4 на одной странице
        val maxImagesToShow = min(images.size, 4)
        val imagesToShow = images.take(maxImagesToShow)
        
        // Определяем количество строк и колонок (2x2 сетка)
        val columns = 2
        val rows = (maxImagesToShow + columns - 1) / columns // Округление вверх
        
        // Рассчитываем размеры для каждого изображения
        val imageWidth = (availableWidth - ((columns - 1) * 10)) / columns // 10px между изображениями
        val imageHeight = min(maxHeight, maxHeight / max(1, rows - 1))
        
        // Рисуем изображения сеткой
        var currentRow = 0
        var currentColumn = 0
        
        for (bitmap in imagesToShow) {
            val scaledBitmap = Bitmap.createScaledBitmap(
                bitmap,
                imageWidth.toInt(),
                imageHeight.toInt(),
                true
            )
            
            val xPosition = pageMargin + (imageWidth + 10) * currentColumn
            val currentYPosition = yPosition + (imageHeight + 15) * currentRow
            
            canvas.drawBitmap(scaledBitmap, xPosition.toFloat(), currentYPosition, null)
            
            // Перемещаемся к следующей колонке или строке
            currentColumn++
            if (currentColumn >= columns) {
                currentColumn = 0
                currentRow++
            }
        }
        
        // Обновляем позицию Y после всех изображений
        yPosition += (imageHeight + 15) * rows + 10
        
        return yPosition
    }
    
    /**
     * Рисует описание объекта
     */
    private fun drawDescription(canvas: Canvas, property: Property, startY: Float): Float {
        if (property.description.isNullOrBlank()) return startY
        
        var yPosition = startY
        
        // Рисуем фоновый прямоугольник
        val descriptionText = property.description
        
        // Создаем объект TextPaint для рисования текста
        val textPaint = TextPaint(regularTextPaint)
        
        // Вычисляем ширину текста
        val textWidth = pageWidth - (pageMargin * 2) - 40 // отступы слева и справа
        
        // Создаем StaticLayout для многострочного текста
        val staticLayout = StaticLayout.Builder
            .obtain(descriptionText, 0, descriptionText.length, textPaint, textWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(5f, 1f)
            .build()
        
        // Высота текста
        val textHeight = staticLayout.height
        
        // Рисуем фоновый прямоугольник
        val descriptionRect = Rect(
            pageMargin,
            yPosition.toInt(),
            pageWidth - pageMargin,
            yPosition.toInt() + textHeight + 40 // отступы сверху и снизу
        )
        canvas.drawRect(descriptionRect, backgroundPaint)
        
        // Сохраняем состояние canvas
        canvas.save()
        
        // Перемещаем точку отсчета для рисования текста
        canvas.translate(pageMargin + 20f, yPosition + 20f)
        
        // Рисуем текст
        staticLayout.draw(canvas)
        
        // Восстанавливаем состояние canvas
        canvas.restore()
        
        // Обновляем позицию Y
        yPosition += textHeight + 40f
        
        return yPosition
    }
    
    /**
     * Рисует особенности и преимущества объекта в компактном формате
     */
    private fun drawFeatures(canvas: Canvas, property: Property, startY: Float): Float {
        var yPosition = startY
        
        val features = mutableListOf<String>()
        
        // Добавляем характеристики
        property.amenities.forEach { amenity ->
            features.add("✓ $amenity")
        }
        
        // Добавляем информацию об инфраструктуре
        property.nearbyObjects.forEach { nearby ->
            features.add("✓ Рядом: $nearby")
        }
        
        // Добавляем информацию о виде из окон
        property.views.forEach { view ->
            features.add("✓ Вид на: $view")
        }
        
        // Проверяем, есть ли особенности для отображения
        if (features.isEmpty()) return yPosition
        
        // Рисуем фоновый прямоугольник
        val featuresHeight = min(200, 20 + features.size * 20) // Ограничиваем высоту
        val featuresRect = Rect(
            pageMargin,
            yPosition.toInt(),
            pageWidth - pageMargin,
            yPosition.toInt() + featuresHeight
        )
        canvas.drawRect(featuresRect, backgroundPaint)
        
        // Определяем количество колонок
        val columns = if (features.size > 8) 2 else 1
        val itemsPerColumn = (features.size + columns - 1) / columns
        
        // Рассчитываем ширину колонки
        val columnWidth = (pageWidth - pageMargin * 2) / columns
        
        // Отступ внутри блока
        yPosition += 20f
        
        // Рисуем особенности по колонкам
        for (columnIndex in 0 until columns) {
            var columnY = yPosition
            val columnX = pageMargin + 10f + columnIndex * columnWidth
            
            for (rowIndex in 0 until itemsPerColumn) {
                val index = columnIndex * itemsPerColumn + rowIndex
                if (index < features.size) {
                    val featureText = features[index]
                    
                    // Рисуем текст особенности
                    canvas.drawText(
                        featureText,
                        columnX,
                        columnY,
                        regularTextPaint
                    )
                    
                    // Переходим к следующей строке
                    columnY += 20f
                }
            }
        }
        
        // Возвращаем новую позицию Y
        return yPosition + featuresHeight - 20f
    }
    
    /**
     * Рисует раздел с адресом
     */
    private fun drawAddressSection(canvas: Canvas, property: Property, startY: Float): Float {
        var yPosition = startY
        
        // Рисуем заголовок раздела
        yPosition = drawSectionHeader(canvas, "Адрес", yPosition)
        
        // Определяем высоту блока
        val boxHeight = 100f // Фиксированная высота для адреса
        
        // Рисуем фоновый прямоугольник
        val addressRect = Rect(
            pageMargin,
            yPosition.toInt(),
            pageWidth - pageMargin,
            yPosition.toInt() + boxHeight.toInt()
        )
        canvas.drawRect(addressRect, backgroundPaint)
        
        // Добавляем отступ внутри блока
        yPosition += 20f
        val textX = pageMargin + 20f
        
        // Извлекаем только название улицы из полного адреса
        val streetName = extractStreetName(property.address)
        
        // Выводим только улицу вместо полного адреса
        canvas.drawText(
            "Адрес: $streetName",
            textX,
            yPosition,
            boldTextPaint
        )
        yPosition += 25f
        
        // Район
        if (property.district.isNotEmpty()) {
            canvas.drawText(
                "Район: ${property.district}",
                textX,
                yPosition,
                regularTextPaint
            )
            yPosition += 20f
        }
        
        // Возвращаем новую позицию Y
        return startY + boxHeight
    }
    
    /**
     * Рисует раздел с характеристиками объекта
     */
    private fun drawFeaturesSection(canvas: Canvas, property: Property, startY: Float): Float {
        var yPosition = startY
        
        // Рисуем заголовок раздела
        yPosition = drawSectionHeader(canvas, "Характеристики", yPosition)
        
        // Собираем все характеристики в список
        val features = mutableListOf<Pair<String, String>>()
        
        // Основные характеристики
        if (property.propertyType.isNotEmpty()) {
            features.add("Тип недвижимости" to property.propertyType)
        }
        
        if (property.roomsCount > 0) {
            features.add("Количество комнат" to property.roomsCount.toString())
        }
        
        if (property.isStudio) {
            features.add("Планировка" to "Студия")
        } else if (property.layout.isNotEmpty()) {
            features.add("Планировка" to property.layout)
        }
        
        if (property.area > 0) {
            features.add("Площадь" to "${property.area} м²")
        }
        
        if (property.floor > 0) {
            features.add("Этаж" to property.floor.toString())
        }
        
        if (property.totalFloors > 0) {
            features.add("Этажность дома" to property.totalFloors.toString())
        }
        
        // Особенности типа недвижимости
        if (property.levelsCount > 0) {
            features.add("Количество уровней" to property.levelsCount.toString())
        }
        
        if (property.landArea > 0) {
            features.add("Площадь участка" to "${property.landArea} соток")
        }
        
        if (property.hasGarage) {
            features.add("Гараж" to "Есть")
        }
        
        if (property.garageSpaces > 0) {
            features.add("Мест в гараже" to property.garageSpaces.toString())
        }
        
        if (property.hasBathhouse) {
            features.add("Баня" to "Есть")
        }
        
        if (property.hasPool) {
            features.add("Бассейн" to if (property.poolType.isNotEmpty()) property.poolType else "Есть")
        }
        
        // Внутреннее состояние
        if (property.repairState.isNotEmpty()) {
            features.add("Ремонт" to property.repairState)
        }
        
        if (!property.noFurniture) {
            features.add("Мебель" to "Есть")
        }
        
        if (property.hasAppliances) {
            features.add("Бытовая техника" to "Есть")
        }
        
        if (property.bathroomsCount != null && property.bathroomsCount > 0) {
            features.add("Количество санузлов" to property.bathroomsCount.toString())
        }
        
        if (property.bathroomType.isNotEmpty()) {
            features.add("Тип санузла" to property.bathroomType)
        }
        
        if (property.heatingType.isNotEmpty()) {
            features.add("Отопление" to property.heatingType)
        }
        
        if (property.balconiesCount > 0) {
            features.add("Балконы" to property.balconiesCount.toString())
        }
        
        // Парковка
        if (property.hasParking) {
            features.add("Парковка" to if (property.parkingType != null) property.parkingType else "Есть")
        }
        
        if (property.parkingSpaces != null && property.parkingSpaces > 0) {
            features.add("Парковочных мест" to property.parkingSpaces.toString())
        }
        
        if (property.elevatorsCount != null && property.elevatorsCount > 0) {
            features.add("Лифты" to property.elevatorsCount.toString())
        }
        
        // Определяем высоту блока на основе количества характеристик
        val itemsPerColumn = (features.size + 2) / 3 // Разделяем на 3 колонки
        val boxHeight = 20f + (itemsPerColumn * 20f) + 20f // отступы + высота строк + отступ снизу
        
        // Рисуем фоновый прямоугольник
        val featuresRect = Rect(
            pageMargin,
            yPosition.toInt(),
            pageWidth - pageMargin,
            yPosition.toInt() + boxHeight.toInt()
        )
        canvas.drawRect(featuresRect, backgroundPaint)
        
        // Добавляем отступ внутри блока
        yPosition += 20f
        
        // Определяем ширину колонки
        val columnWidth = contentWidth / 3
        
        // Рисуем характеристики по колонкам
        for (i in features.indices) {
            val columnIndex = i / itemsPerColumn
            val rowIndex = i % itemsPerColumn
            
            val x = pageMargin + 10f + (columnIndex * columnWidth)
            val y = yPosition + (rowIndex * 20f)
            
            val (label, value) = features[i]
            
            canvas.drawText(
                "$label: $value",
                x,
                y,
                regularTextPaint
            )
        }
        
        // Возвращаем новую позицию Y
        return startY + boxHeight
    }
    
    /**
     * Рисует раздел с описанием объекта
     */
    private fun drawDescriptionSection(canvas: Canvas, property: Property, startY: Float): Float {
        if (property.description.isNullOrBlank()) return startY
        
        var yPosition = startY
        
        // Рисуем заголовок раздела
        yPosition = drawSectionHeader(canvas, "Описание", yPosition)
        
        // Создаем TextPaint для многострочного текста
        val textPaint = TextPaint(regularTextPaint)
        
        // Вычисляем ширину текста
        val textWidth = contentWidth - 40 // отступы слева и справа
        
        // Создаем StaticLayout для многострочного текста
        val staticLayout = StaticLayout.Builder
            .obtain(property.description, 0, property.description.length, textPaint, textWidth.toInt())
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(5f, 1f)
            .build()
        
        // Высота текста
        val textHeight = staticLayout.height
        
        // Определяем высоту блока
        val boxHeight = textHeight + 40f // Отступы сверху и снизу
        
        // Рисуем фоновый прямоугольник
        val descriptionRect = Rect(
            pageMargin,
            yPosition.toInt(),
            pageWidth - pageMargin,
            yPosition.toInt() + boxHeight.toInt()
        )
        canvas.drawRect(descriptionRect, backgroundPaint)
        
        // Сохраняем состояние canvas
        canvas.save()
        
        // Перемещаем точку отсчета для рисования текста
        canvas.translate(pageMargin + 20f, yPosition + 20f)
        
        // Рисуем текст
        staticLayout.draw(canvas)
        
        // Восстанавливаем состояние canvas
        canvas.restore()
        
        // Возвращаем новую позицию Y
        return startY + boxHeight
    }
    
    /**
     * Рисует раздел с удобствами и особенностями
     */
    private fun drawAmenitiesSection(canvas: Canvas, property: Property, startY: Float): Float {
        var yPosition = startY
        
        // Рисуем заголовок раздела
        yPosition = drawSectionHeader(canvas, "Удобства и особенности", yPosition)
        
        // Собираем все удобства и особенности
        val amenities = mutableListOf<String>()
        
        // Добавляем удобства
        property.amenities.forEach { amenity ->
            amenities.add("• $amenity")
        }
        
        // Добавляем ближайшие объекты
        property.nearbyObjects.forEach { nearby ->
            amenities.add("• Рядом: $nearby")
        }
        
        // Добавляем виды из окон
        property.views.forEach { view ->
            amenities.add("• Вид: $view")
        }
        
        // Определяем высоту блока на основе количества особенностей
        val itemsPerColumn = if (amenities.size > 10) (amenities.size + 1) / 2 else amenities.size
        val boxHeight = 20f + (itemsPerColumn * 20f) + 20f // отступы + высота строк + отступ снизу
        
        // Рисуем фоновый прямоугольник
        val amenitiesRect = Rect(
            pageMargin,
            yPosition.toInt(),
            pageWidth - pageMargin,
            yPosition.toInt() + boxHeight.toInt()
        )
        canvas.drawRect(amenitiesRect, backgroundPaint)
        
        // Добавляем отступ внутри блока
        yPosition += 20f
        
        // Определяем количество колонок и ширину колонки
        val columns = if (amenities.size > 10) 2 else 1
        val columnWidth = contentWidth / columns
        
        // Рисуем удобства по колонкам
        for (i in amenities.indices) {
            val columnIndex = if (columns == 1) 0 else i / itemsPerColumn
            val rowIndex = if (columns == 1) i else i % itemsPerColumn
            
            val x = pageMargin + 10f + (columnIndex * columnWidth)
            val y = yPosition + (rowIndex * 20f)
            
            canvas.drawText(
                amenities[i],
                x,
                y,
                regularTextPaint
            )
        }
        
        // Возвращаем новую позицию Y
        return startY + boxHeight
    }
    
    /**
     * Рассчитывает высоту блока с адресом
     */
    private fun calculateAddressHeight(property: Property): Float {
        // Заголовок раздела + фиксированная высота блока
        return sectionHeaderHeight + 100f
    }
    
    /**
     * Рассчитывает высоту блока с характеристиками
     */
    private fun calculateFeaturesHeight(property: Property): Float {
        // Собираем все характеристики, как в методе drawFeaturesSection
        val features = mutableListOf<Pair<String, String>>()
        
        // Основные характеристики
        if (property.propertyType.isNotEmpty()) features.add("Тип недвижимости" to property.propertyType)
        if (property.roomsCount > 0) features.add("Количество комнат" to property.roomsCount.toString())
        if (property.isStudio) {
            features.add("Планировка" to "Студия")
        } else if (property.layout.isNotEmpty()) {
            features.add("Планировка" to property.layout)
        }
        if (property.area > 0) features.add("Площадь" to "${property.area} м²")
        if (property.floor > 0) features.add("Этаж" to property.floor.toString())
        if (property.totalFloors > 0) features.add("Этажность дома" to property.totalFloors.toString())
        
        // Особенности типа недвижимости
        if (property.levelsCount > 0) features.add("Количество уровней" to property.levelsCount.toString())
        if (property.landArea > 0) features.add("Площадь участка" to "${property.landArea} соток")
        if (property.hasGarage) features.add("Гараж" to "Есть")
        if (property.garageSpaces > 0) features.add("Мест в гараже" to property.garageSpaces.toString())
        if (property.hasBathhouse) features.add("Баня" to "Есть")
        if (property.hasPool) features.add("Бассейн" to if (property.poolType.isNotEmpty()) property.poolType else "Есть")
        
        // Внутреннее состояние
        if (property.repairState.isNotEmpty()) features.add("Ремонт" to property.repairState)
        if (!property.noFurniture) features.add("Мебель" to "Есть")
        if (property.hasAppliances) features.add("Бытовая техника" to "Есть")
        if (property.bathroomsCount != null && property.bathroomsCount > 0) features.add("Количество санузлов" to property.bathroomsCount.toString())
        if (property.bathroomType.isNotEmpty()) features.add("Тип санузла" to property.bathroomType)
        if (property.heatingType.isNotEmpty()) features.add("Отопление" to property.heatingType)
        if (property.balconiesCount > 0) features.add("Балконы" to property.balconiesCount.toString())
        
        // Парковка
        if (property.hasParking) features.add("Парковка" to if (property.parkingType != null) property.parkingType else "Есть")
        if (property.parkingSpaces != null && property.parkingSpaces > 0) features.add("Парковочных мест" to property.parkingSpaces.toString())
        if (property.elevatorsCount != null && property.elevatorsCount > 0) features.add("Лифты" to property.elevatorsCount.toString())
        
        // Определяем высоту блока на основе количества характеристик
        val itemsPerColumn = (features.size + 2) / 3 // Разделяем на 3 колонки
        val boxHeight = 20f + (itemsPerColumn * 20f) + 20f // отступы + высота строк + отступ снизу
        
        // Заголовок раздела + высота блока
        return sectionHeaderHeight + boxHeight
    }
    
    /**
     * Рассчитывает высоту блока с описанием
     */
    private fun calculateDescriptionHeight(property: Property): Float {
        if (property.description.isNullOrBlank()) return 0f
        
        // Создаем TextPaint для многострочного текста
        val textPaint = TextPaint(regularTextPaint)
        
        // Вычисляем ширину текста
        val textWidth = contentWidth - 40 // отступы слева и справа
        
        // Создаем StaticLayout для многострочного текста
        val staticLayout = StaticLayout.Builder
            .obtain(property.description, 0, property.description.length, textPaint, textWidth.toInt())
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(5f, 1f)
            .build()
        
        // Высота текста
        val textHeight = staticLayout.height
        
        // Заголовок раздела + высота текста + отступы
        return sectionHeaderHeight + textHeight + 40f
    }
    
    /**
     * Рассчитывает высоту блока с удобствами
     */
    private fun calculateAmenitiesHeight(property: Property): Float {
        // Собираем все удобства и особенности
        val amenities = mutableListOf<String>()
        
        // Добавляем удобства
        amenities.addAll(property.amenities.map { "• $it" })
        
        // Добавляем ближайшие объекты
        amenities.addAll(property.nearbyObjects.map { "• Рядом: $it" })
        
        // Добавляем виды из окон
        amenities.addAll(property.views.map { "• Вид: $it" })
        
        // Если нет удобств, возвращаем 0
        if (amenities.isEmpty()) return 0f
        
        // Определяем высоту блока на основе количества особенностей
        val itemsPerColumn = if (amenities.size > 10) (amenities.size + 1) / 2 else amenities.size
        val boxHeight = 20f + (itemsPerColumn * 20f) + 20f // отступы + высота строк + отступ снизу
        
        // Заголовок раздела + высота блока
        return sectionHeaderHeight + boxHeight
    }
    
    /**
     * Извлекает название улицы из полного адреса, безопасно удаляя номера домов
     */
    private fun extractStreetName(address: String): String {
        // Проверка на пустой адрес
        if (address.isBlank()) return "Адрес не указан"
        
        // Типичные префиксы улиц в России
        val streetPrefixes = listOf("улица", "ул", "проспект", "пр-т", "пр", "бульвар", "б-р", 
            "шоссе", "ш", "переулок", "пер", "проезд", "набережная", "наб", "площадь", "пл")
            
        // Шаблон для поиска улиц с префиксом
        val streetPattern = "(${streetPrefixes.joinToString("|")})\\s+[\\w\\s-]+"
        
        // Разбиваем адрес по запятым
        val parts = address.split(",").map { it.trim() }
        if (parts.isEmpty()) return address.trim()
        
        // Ищем часть адреса, которая содержит название улицы
        for (part in parts) {
            // Поиск по шаблону с префиксом улицы
            val streetMatch = Regex(streetPattern, RegexOption.IGNORE_CASE).find(part)
            if (streetMatch != null) {
                // Удаляем номера домов из найденной улицы
                val streetNameWithoutNumber = streetMatch.value.replace(
                    Regex(",?\\s*\\d+[а-яА-Я]?(/\\d+)?\\s*$"),
                    ""
                ).trim()
                
                return streetNameWithoutNumber
            }
        }
        
        // Если не нашли по шаблону с префиксом, проверяем первую часть адреса
        if (parts.isNotEmpty()) {
            val firstPart = parts[0]
            
            // Удаляем номера домов из первой части адреса
            val firstPartWithoutNumber = firstPart.replace(
                Regex(",?\\s*\\d+[а-яА-Я]?(/\\d+)?\\s*$"),
                ""
            ).trim()
            
            if (firstPartWithoutNumber.isNotBlank()) {
                return firstPartWithoutNumber
            }
        }
        
        // Если не удалось извлечь улицу, возвращаем общее название
        return "улица"
    }
    
    /**
     * Форматирует цену для отображения
     */
    private fun formatPrice(price: Double): String {
        return String.format("%,d", price.toInt()).replace(",", " ")
    }
    
    /**
     * Рассчитывает примерное пространство, необходимое для изображений
     */
    private fun calculateImagesSpace(images: List<Bitmap>): Float {
        if (images.isEmpty()) return 0f
        
        // Максимально 4 изображения, 2 в ряд
        val maxImagesToShow = min(images.size, 4)
        val columns = 2
        val rows = (maxImagesToShow + columns - 1) / columns // Округление вверх
        
        // Высота заголовка раздела + высота изображений + отступы
        return sectionHeaderHeight.toFloat() + (150f * rows) + (15f * (rows + 1))
    }
    
    /**
     * Рассчитывает примерное пространство, необходимое для особенностей и преимуществ
     */
    private fun calculateFeaturesSpace(property: Property): Float {
        val features = property.amenities.size + property.nearbyObjects.size + property.views.size
        
        if (features == 0) return 0f
        
        // Ограничиваем максимальную высоту
        val featuresHeight = min(200f, 20f + features * 20f)
        
        // Высота заголовка раздела + высота блока особенностей
        return sectionHeaderHeight.toFloat() + featuresHeight
    }
    
    /**
     * Рассчитывает примерное пространство, необходимое для описания
     */
    private fun calculateDescriptionSpace(property: Property): Float {
        if (property.description.isNullOrBlank()) return 0f
        
        // Примерно оцениваем размер на основе длины текста
        // 1 строка ~ 80 символов, высота строки ~ 20px
        val textWidth = pageWidth - (pageMargin * 2)
        val charsPerLine = (textWidth / 8).toInt() // 8px на символ в среднем
        val lines = (property.description.length + charsPerLine - 1) / charsPerLine
        
        // Высота заголовка + высота текста + отступы
        return sectionSpacing.toFloat() + (lines * 20f) + sectionSpacing.toFloat()
    }
    
    /**
     * Рисует раздел с фотографиями
     */
    private fun drawPhotosSection(canvas: Canvas, images: List<Bitmap>, startY: Float): Float {
        if (images.isEmpty()) return startY
        
        var yPosition = startY
        
        // Рисуем заголовок раздела
        yPosition = drawSectionHeader(canvas, "Фотографии", yPosition)
        
        // Определяем размеры для галереи изображений
        val availableWidth = contentWidth
        val imageWidth = (availableWidth - 20) / 2 // 2 колонки с отступом 20px между ними
        val imageHeight = 120f // Фиксированная высота для сохранения пропорций
        
        // Рисуем изображения в сетке 2x2
        val maxImagesToShow = min(4, images.size)
        val rows = (maxImagesToShow + 1) / 2 // Округление вверх
        
        // Рисуем фоновый прямоугольник
        val photosBoxHeight = rows * (imageHeight + 10) + 30 // Высота с учетом отступов
        val photosRect = Rect(
            pageMargin,
            yPosition.toInt(),
            pageWidth - pageMargin,
            yPosition.toInt() + photosBoxHeight.toInt()
        )
        canvas.drawRect(photosRect, backgroundPaint)
        
        // Добавляем отступ внутри блока
        yPosition += 15f
        
        // Рисуем изображения
        for (i in 0 until maxImagesToShow) {
            val row = i / 2
            val col = i % 2
            
            val x = pageMargin + col * (imageWidth + 20) + 10
            val y = yPosition + row * (imageHeight + 10)
            
            // Масштабируем изображение
            val bitmap = images[i]
            val scaledBitmap = Bitmap.createScaledBitmap(
                bitmap,
                imageWidth.toInt(),
                imageHeight.toInt(),
                true
            )
            
            // Рисуем изображение
            canvas.drawBitmap(scaledBitmap, x.toFloat(), y, null)
        }
        
        // Возвращаем новую позицию Y
        return yPosition + photosBoxHeight - 15f
    }
    
    /**
     * Рисует раздел с ценой и условиями
     */
    private fun drawPriceInfo(canvas: Canvas, property: Property, startY: Float): Float {
        var yPosition = startY
        
        // Рисуем заголовок раздела
        yPosition = drawSectionHeader(canvas, "Условия аренды", yPosition)
        
        // Определяем, какие типы аренды доступны
        val hasLongTerm = property.monthlyRent != null && property.monthlyRent > 0
        val hasShortTerm = property.dailyPrice != null && property.dailyPrice > 0
        
        // Расчет высоты блока (приблизительно)
        var boxHeight = 20f // Начальный отступ
        
        // Для долгосрочной аренды
        if (hasLongTerm) {
            boxHeight += 40f // Заголовок подраздела
            boxHeight += 25f // Стоимость аренды
            
            if (property.winterMonthlyRent != null && property.winterMonthlyRent > 0) boxHeight += 20f
            if (property.summerMonthlyRent != null && property.summerMonthlyRent > 0) boxHeight += 20f
            if (property.minRentPeriod != null) boxHeight += 20f
            
            if (property.securityDeposit != null && property.securityDeposit > 0) {
                boxHeight += 20f
            } else if (property.depositCustomAmount != null && property.depositCustomAmount > 0) {
                boxHeight += 20f
            }
            
            if (property.utilitiesIncluded || property.utilitiesCost != null) boxHeight += 20f
        }
        
        // Добавляем разделитель, если есть оба типа аренды
        if (hasLongTerm && hasShortTerm) boxHeight += 30f
        
        // Для краткосрочной аренды
        if (hasShortTerm) {
            if (hasLongTerm) boxHeight += 40f // Заголовок подраздела
            boxHeight += 25f // Стоимость аренды
            
            if (property.weekdayPrice != null && property.weekdayPrice > 0) boxHeight += 20f
            if (property.weekendPrice != null && property.weekendPrice > 0) boxHeight += 20f
            if (property.minStayDays != null) boxHeight += 20f
            
            if (property.shortTermDeposit != null && property.shortTermDeposit > 0) {
                boxHeight += 20f
            } else if (property.shortTermDepositCustomAmount != null && property.shortTermDepositCustomAmount > 0) {
                boxHeight += 20f
            }
            
            if (property.maxGuests != null) boxHeight += 20f
            if (property.bedsCount != null) boxHeight += 20f
        }
        
        // Общие условия
        boxHeight += 30f // Заголовок подраздела
        boxHeight += 20f // Дети
        boxHeight += 20f // Животные
        boxHeight += 20f // Курение
        
        // Рисуем фоновый прямоугольник
        val priceRect = Rect(
            pageMargin,
            yPosition.toInt(),
            pageWidth - pageMargin,
            yPosition.toInt() + boxHeight.toInt()
        )
        canvas.drawRect(priceRect, backgroundPaint)
        
        // Добавляем отступ внутри блока
        yPosition += 20f
        val textX = pageMargin + 20f
        
        // Долгосрочная аренда
        if (hasLongTerm) {
            if (hasShortTerm) {
                // Если есть оба типа аренды, добавляем подзаголовок
                canvas.drawText("Долгосрочная аренда", textX, yPosition, boldTextPaint)
                yPosition += 30f
            }
            
            // Стоимость аренды
            canvas.drawText(
                "Стоимость аренды: ${formatPrice(property.monthlyRent!!)} ₽/месяц",
                textX,
                yPosition,
                highlightTextPaint
            )
            yPosition += 25f
            
            // Сезонные цены
            if (property.winterMonthlyRent != null && property.winterMonthlyRent > 0) {
                canvas.drawText(
                    "Стоимость зимой: ${formatPrice(property.winterMonthlyRent)} ₽/месяц",
                    textX,
                    yPosition,
                    regularTextPaint
                )
                yPosition += 20f
            }
            
            if (property.summerMonthlyRent != null && property.summerMonthlyRent > 0) {
                canvas.drawText(
                    "Стоимость летом: ${formatPrice(property.summerMonthlyRent)} ₽/месяц",
                    textX,
                    yPosition,
                    regularTextPaint
                )
                yPosition += 20f
            }
            
            // Минимальный срок
            if (property.minRentPeriod != null) {
                canvas.drawText(
                    "Минимальный срок: ${property.minRentPeriod}",
                    textX,
                    yPosition,
                    regularTextPaint
                )
                yPosition += 20f
            }
            
            // Залог
            if (property.securityDeposit != null && property.securityDeposit > 0) {
                canvas.drawText(
                    "Залог: ${formatPrice(property.securityDeposit)} ₽",
                    textX,
                    yPosition,
                    regularTextPaint
                )
                yPosition += 20f
            } else if (property.depositCustomAmount != null && property.depositCustomAmount > 0) {
                canvas.drawText(
                    "Залог: ${formatPrice(property.depositCustomAmount)} ₽",
                    textX,
                    yPosition,
                    regularTextPaint
                )
                yPosition += 20f
            }
            
            // Коммунальные платежи
            if (property.utilitiesIncluded) {
                canvas.drawText(
                    "Коммунальные платежи: включены в стоимость",
                    textX,
                    yPosition,
                    regularTextPaint
                )
                yPosition += 20f
            } else if (property.utilitiesCost != null && property.utilitiesCost > 0) {
                canvas.drawText(
                    "Коммунальные платежи: ${formatPrice(property.utilitiesCost)} ₽/месяц",
                    textX,
                    yPosition,
                    regularTextPaint
                )
                yPosition += 20f
            }
        }
        
        // Разделитель, если есть оба типа аренды
        if (hasLongTerm && hasShortTerm) {
            // Горизонтальная линия
            canvas.drawLine(
                pageMargin + 20f,
                yPosition + 10f,
                pageWidth - pageMargin - 20f,
                yPosition + 10f,
                dividerPaint
            )
            yPosition += 30f
            
            // Подзаголовок для краткосрочной аренды
            canvas.drawText("Посуточная аренда", textX, yPosition, boldTextPaint)
            yPosition += 30f
        }
        
        // Краткосрочная аренда
        if (hasShortTerm) {
            // Стоимость аренды
            canvas.drawText(
                "Стоимость аренды: ${formatPrice(property.dailyPrice!!)} ₽/сутки",
                textX,
                yPosition,
                highlightTextPaint
            )
            yPosition += 25f
            
            // Цены в будни и выходные
            if (property.weekdayPrice != null && property.weekdayPrice > 0) {
                canvas.drawText(
                    "Цена в будни: ${formatPrice(property.weekdayPrice)} ₽/сутки",
                    textX,
                    yPosition,
                    regularTextPaint
                )
                yPosition += 20f
            }
            
            if (property.weekendPrice != null && property.weekendPrice > 0) {
                canvas.drawText(
                    "Цена в выходные: ${formatPrice(property.weekendPrice)} ₽/сутки",
                    textX,
                    yPosition,
                    regularTextPaint
                )
                yPosition += 20f
            }
            
            // Минимальный срок
            if (property.minStayDays != null && property.minStayDays > 0) {
                val daysText = getDaysText(property.minStayDays)
                canvas.drawText(
                    "Минимальный срок: ${property.minStayDays} $daysText",
                    textX,
                    yPosition,
                    regularTextPaint
                )
                yPosition += 20f
            }
            
            // Залог
            if (property.shortTermDeposit != null && property.shortTermDeposit > 0) {
                canvas.drawText(
                    "Залог: ${formatPrice(property.shortTermDeposit)} ₽",
                    textX,
                    yPosition,
                    regularTextPaint
                )
                yPosition += 20f
            } else if (property.shortTermDepositCustomAmount != null && property.shortTermDepositCustomAmount > 0) {
                canvas.drawText(
                    "Залог: ${formatPrice(property.shortTermDepositCustomAmount)} ₽",
                    textX,
                    yPosition,
                    regularTextPaint
                )
                yPosition += 20f
            }
            
            // Максимальное количество гостей
            if (property.maxGuests != null && property.maxGuests > 0) {
                canvas.drawText(
                    "Максимум гостей: ${property.maxGuests}",
                    textX,
                    yPosition,
                    regularTextPaint
                )
                yPosition += 20f
            }
            
            // Количество спальных мест
            if (property.bedsCount != null && property.bedsCount > 0) {
                canvas.drawText(
                    "Спальных мест: ${property.bedsCount}",
                    textX,
                    yPosition,
                    regularTextPaint
                )
                yPosition += 20f
            }
        }
        
        // Общие условия проживания
        if (hasLongTerm || hasShortTerm) {
            // Подзаголовок для общих условий
            canvas.drawText("Условия проживания", textX, yPosition, boldTextPaint)
            yPosition += 30f
            
            // Дети
            canvas.drawText(
                "Проживание с детьми: ${if (property.childrenAllowed) "разрешено" else "запрещено"}",
                textX,
                yPosition,
                regularTextPaint
            )
            yPosition += 20f
            
            // Животные
            canvas.drawText(
                "Проживание с животными: ${if (property.petsAllowed) "разрешено" else "запрещено"}",
                textX,
                yPosition,
                regularTextPaint
            )
            yPosition += 20f
            
            // Курение
            canvas.drawText(
                "Курение: ${if (property.smokingAllowed) "разрешено" else "запрещено"}",
                textX,
                yPosition,
                regularTextPaint
            )
        }
        
        // Возвращаем новую позицию Y
        return startY + boxHeight
    }
    
    /**
     * Получает текстовое представление для количества дней
     */
    private fun getDaysText(days: Int): String {
        return when {
            days % 10 == 1 && days % 100 != 11 -> "день"
            days % 10 in 2..4 && days % 100 !in 12..14 -> "дня"
            else -> "дней"
        }
    }
    
    /**
     * Рассчитывает примерную высоту блока с фотографиями
     */
    private fun calculatePhotosHeight(images: List<Bitmap>): Float {
        if (images.isEmpty()) return 0f
        
        val maxImagesToShow = min(4, images.size)
        val rows = (maxImagesToShow + 1) / 2 // Округление вверх
        
        // Заголовок раздела + высота блока с фотографиями
        return sectionHeaderHeight.toFloat() + (rows * 120f) + ((rows + 1) * 10f) + 30f
    }
    
    /**
     * Рассчитывает примерную высоту блока с ценовой информацией
     */
    private fun calculatePriceInfoHeight(property: Property): Float {
        var height = sectionHeaderHeight.toFloat()
        
        // Определяем, какие типы аренды доступны
        val hasLongTerm = property.monthlyRent != null && property.monthlyRent > 0
        val hasShortTerm = property.dailyPrice != null && property.dailyPrice > 0
        
        // Базовая высота блока
        height += 20f // Начальный отступ
        
        // Для долгосрочной аренды
        if (hasLongTerm) {
            height += if (hasShortTerm) 40f else 0f // Заголовок подраздела, если есть оба типа аренды
            height += 25f // Стоимость аренды
            
            if (property.winterMonthlyRent != null && property.winterMonthlyRent > 0) height += 20f
            if (property.summerMonthlyRent != null && property.summerMonthlyRent > 0) height += 20f
            if (property.minRentPeriod != null) height += 20f
            
            if ((property.securityDeposit != null && property.securityDeposit > 0) || 
                (property.depositCustomAmount != null && property.depositCustomAmount > 0)) {
                height += 20f
            }
            
            if (property.utilitiesIncluded || (property.utilitiesCost != null && property.utilitiesCost > 0)) {
                height += 20f
            }
        }
        
        // Разделитель, если есть оба типа аренды
        if (hasLongTerm && hasShortTerm) height += 30f
        
        // Для краткосрочной аренды
        if (hasShortTerm) {
            height += if (hasLongTerm) 40f else 0f // Заголовок подраздела, если есть оба типа аренды
            height += 25f // Стоимость аренды
            
            if (property.weekdayPrice != null && property.weekdayPrice > 0) height += 20f
            if (property.weekendPrice != null && property.weekendPrice > 0) height += 20f
            if (property.minStayDays != null && property.minStayDays > 0) height += 20f
            
            if ((property.shortTermDeposit != null && property.shortTermDeposit > 0) || 
                (property.shortTermDepositCustomAmount != null && property.shortTermDepositCustomAmount > 0)) {
                height += 20f
            }
            
            if (property.maxGuests != null && property.maxGuests > 0) height += 20f
            if (property.bedsCount != null && property.bedsCount > 0) height += 20f
        }
        
        // Общие условия
        if (hasLongTerm || hasShortTerm) {
            height += 30f // Заголовок подраздела
            height += 60f // Дети, животные, курение
        }
        
        return height
    }
} 