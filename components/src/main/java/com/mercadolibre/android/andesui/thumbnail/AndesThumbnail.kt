package com.mercadolibre.android.andesui.thumbnail

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.content.res.AppCompatResources
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.mercadolibre.android.andesui.R
import com.mercadolibre.android.andesui.color.AndesColor
import com.mercadolibre.android.andesui.thumbnail.factory.AndesThumbnailAttrs
import com.mercadolibre.android.andesui.thumbnail.factory.AndesThumbnailAttrsParser
import com.mercadolibre.android.andesui.thumbnail.factory.AndesThumbnailConfiguration
import com.mercadolibre.android.andesui.thumbnail.factory.AndesThumbnailConfigurationFactory
import com.mercadolibre.android.andesui.thumbnail.hierarchy.AndesThumbnailHierarchy
import com.mercadolibre.android.andesui.thumbnail.size.AndesThumbnailSize
import com.mercadolibre.android.andesui.thumbnail.state.AndesThumbnailState
import com.mercadolibre.android.andesui.thumbnail.type.AndesThumbnailType
import java.io.FileNotFoundException

class AndesThumbnail : FrameLayout {

    /**
     * Getter and setter for [type].
     */
    var type: AndesThumbnailType
        get() = andesThumbnailAttrs.andesThumbnailType
        set(value) {
            andesThumbnailAttrs = andesThumbnailAttrs.copy(andesThumbnailType = value)
            setupBackground(createConfig())
        }

    var size: AndesThumbnailSize
        get() = andesThumbnailAttrs.andesThumbnailSize
        set(value) {
            andesThumbnailAttrs = andesThumbnailAttrs.copy(andesThumbnailSize = value)
            setupBackground(createConfig())
        }

    private lateinit var andesThumbnailAttrs: AndesThumbnailAttrs

    @Suppress("unused")
    private constructor(context: Context) : super(context) {
        throw IllegalStateException("Constructor without parameters in Andes Thumbnail is not allowed." +
            " You must provide some attributes.")
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(attrs)
    }

    @Suppress("unused")
    constructor(
        context: Context,
        accentColor: AndesColor,
        fallbackImage: String,
        hierarchy: AndesThumbnailHierarchy = AndesThumbnailHierarchy.LOUD,
        image: String,
        type: AndesThumbnailType = AndesThumbnailType.ICON,
        size: AndesThumbnailSize = AndesThumbnailSize.SIZE_48,
        state: AndesThumbnailState = AndesThumbnailState.ENABLED
    ) : super(context) {
        initAttrs(accentColor, fallbackImage, hierarchy, image, type, size, state)
    }

    /**
     * Sets the proper [config] for this message based on the [attrs] received via XML.
     *
     * @param attrs attributes from the XML.
     */
    private fun initAttrs(attrs: AttributeSet?) {
        andesThumbnailAttrs = AndesThumbnailAttrsParser.parse(context, attrs)
        val config = AndesThumbnailConfigurationFactory.create(context, andesThumbnailAttrs)
        setupComponents(config)
    }

    private fun initAttrs(
        accentColor: AndesColor,
        fallbackImage: String,
        hierarchy: AndesThumbnailHierarchy,
        image: String,
        type: AndesThumbnailType,
        size: AndesThumbnailSize,
        state: AndesThumbnailState
    ) {
        andesThumbnailAttrs = AndesThumbnailAttrs(accentColor, fallbackImage, hierarchy, image, type, size, state)
        val config = AndesThumbnailConfigurationFactory.create(context, andesThumbnailAttrs)
        setupComponents(config)
    }

    /**
     * Responsible for setting up all properties of each component that is part of this badge.
     * Is like a choreographer ;)
     */
    private fun setupComponents(config: AndesThumbnailConfiguration) {
        initComponents()
        setupViewId()

        setupBackground(config)
        setupImage(config)
    }

    /**
     * Creates all the views that are part of this badge.
     * After a view is created then a view id is added to it.
     */
    private fun initComponents() {
        LayoutInflater.from(context).inflate(R.layout.andes_layout_thumbnail, this)
    }

    /**
     * Sets a view id to this badge.
     */
    private fun setupViewId() {
        if (id == NO_ID) { // If this view has no id
            id = View.generateViewId()
        }
    }

    private fun setupBackground(config: AndesThumbnailConfiguration) {
        val shape = GradientDrawable()
        (shape.mutate() as GradientDrawable).cornerRadius = config.size
        val dp1 = (1 * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
        if (config.hasBorder) {
            shape.setStroke(dp1, config.borderColor.colorIntToAlpha(context))
        } else {
            shape.setColor(config.backgroundColor.colorIntToAlpha(context))
        }

        background = shape

        if (layoutParams == null) {
            layoutParams = LayoutParams(config.size.toInt(), config.size.toInt())
        }
    }

    private fun setupImage(config: AndesThumbnailConfiguration) {
        val resId = context.resources.getIdentifier(config.image, "drawable", context.packageName)
        try {
            ContextCompat.getDrawable(context, resId)
        } catch (e: FileNotFoundException) {
            Log.e("Andes UI", "File $config.image was not found.", e)
            null
        }

        val unwrappedDrawable = AppCompatResources.getDrawable(context, resId)
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
        DrawableCompat.setTint(wrappedDrawable, config.iconColor.colorInt(context))
        val imageFrame = findViewById<ImageView>(R.id.andes_thumbnail_image)
        imageFrame.setBackgroundResource(resId)
        imageFrame.layoutParams = LayoutParams(config.iconSize, config.iconSize, Gravity.CENTER)
    }

    private fun createConfig() = AndesThumbnailConfigurationFactory.create(context, andesThumbnailAttrs)
}
