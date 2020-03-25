package com.mercadolibre.android.andesui.textfield

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.support.constraint.ConstraintLayout
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.mercadolibre.android.andesui.R
import com.mercadolibre.android.andesui.button.AndesButton
import com.mercadolibre.android.andesui.color.toAndesColor
import com.mercadolibre.android.andesui.icons.OfflineIconProvider
import com.mercadolibre.android.andesui.textfield.content.AndesTextfieldLeftContent
import com.mercadolibre.android.andesui.textfield.content.AndesTextfieldRightContent
import com.mercadolibre.android.andesui.textfield.factory.*
import com.mercadolibre.android.andesui.textfield.state.AndesTextfieldState
import com.mercadolibre.android.andesui.utils.buildColoredBitmapDrawable


class AndesTextfield : ConstraintLayout {

    /**
     * Getter and setter for [label].
     */
    var label: String?
        get() = andesTextfieldAttrs.label
        set(value) {
            andesTextfieldAttrs = andesTextfieldAttrs.copy(label = value)
            setupLabelComponent(createConfig())
        }

    /**
     * Getter and setter for [helper].
     */
    var helper: String?
        get() = andesTextfieldAttrs.helper
        set(value) {
            andesTextfieldAttrs = andesTextfieldAttrs.copy(helper = value)
            setupHelperComponent(createConfig())
        }

    /**
     * Getter and setter for [placeholder].
     */
    var placeholder: String?
        get() = andesTextfieldAttrs.placeholder
        set(value) {
            andesTextfieldAttrs = andesTextfieldAttrs.copy(placeholder = value)
            setupPlaceHolderComponent(createConfig())
        }

    /**
     * Getter and setter for [counter].
     */
    var counter: AndesTextfieldCounter?
        get() = andesTextfieldAttrs.counter
        set(value) {
            andesTextfieldAttrs = andesTextfieldAttrs.copy(counter = value)
            setupCounterComponent(createConfig())
        }

    /**
     * Getter and setter for the state of [EditText].
     */
    var state: AndesTextfieldState
        get() = andesTextfieldAttrs.state
        set(value) {
            andesTextfieldAttrs = andesTextfieldAttrs.copy(state = value)
            setupColorComponents(createConfig())
        }

    var leftContent: AndesTextfieldLeftContent?
        get() = andesTextfieldAttrs.leftContent
        set(value) {
            andesTextfieldAttrs = andesTextfieldAttrs.copy(leftContent = value)
            setupLeftComponent(createConfig())
        }

    var rightContent: AndesTextfieldRightContent?
        get() = andesTextfieldAttrs.rightContent
        set(value) {
            andesTextfieldAttrs = andesTextfieldAttrs.copy(rightContent = value)
            setupRightComponent(createConfig())
        }

    private lateinit var andesTextfieldAttrs: AndesTextfieldAttrs
    private lateinit var textfieldContainer: ConstraintLayout
    private lateinit var textContainer: ConstraintLayout
    private lateinit var labelComponent: TextView
    private lateinit var helperComponent: TextView
    private lateinit var counterComponent: TextView
    private lateinit var textComponent: EditText
    private lateinit var iconComponent: SimpleDraweeView
    private lateinit var leftComponent: FrameLayout
    private lateinit var rightComponent: FrameLayout

    @Suppress("unused")
    private constructor(context: Context) : super(context) {
        initAttrs(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs) {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        andesTextfieldAttrs = AndesTextfieldAttrsParser.parse(context, attrs)
        val config = AndesTextfieldConfigurationFactory.create(context, andesTextfieldAttrs)
        setupComponents(config)
    }

    private fun initAttrs(label: String?, helper: String?, placeholder: String?, counter: AndesTextfieldCounter?, state: AndesTextfieldState, leftContent: AndesTextfieldLeftContent?, rightContent: AndesTextfieldRightContent?) {
        andesTextfieldAttrs = AndesTextfieldAttrs(label, helper, placeholder, counter, state, leftContent, rightContent)
        val config = AndesTextfieldConfigurationFactory.create(context, andesTextfieldAttrs)
        setupComponents(config)
    }

    private fun setupComponents(config: AndesTextfieldConfiguration) {
        initComponents()
        setupViewId()
        setupViewAsClickable()
        setupEnabledView()
        setupLabelComponent(config)
        setupHelperComponent(config)
        setupCounterComponent(config)
        setupPlaceHolderComponent(config)
        setupColorComponents(config)
        setupLeftComponent(config)
        setupRightComponent(config)
    }

    /**
     * Creates all the views that are part of this texfield.
     * After a view is created then a view id is added to it.
     *
     */
    private fun initComponents() {
        val container = LayoutInflater.from(context).inflate(R.layout.andes_layout_texfield, this, true)

        textfieldContainer = container.findViewById(R.id.andes_textfield_container)
        textContainer = container.findViewById(R.id.andes_textfield_text_container)
        labelComponent = container.findViewById(R.id.andes_texfield_label)
        helperComponent = container.findViewById(R.id.andes_texfield_helper)
        counterComponent = container.findViewById(R.id.andes_texfield_counter)
        iconComponent = container.findViewById(R.id.andes_texfield_icon)
        textComponent = container.findViewById(R.id.andes_textfield_edittext)
        leftComponent = container.findViewById(R.id.andes_textfield_left_component)
        rightComponent = container.findViewById(R.id.andes_textfield_right_component)
    }

    private fun setupViewId() {
        if (id == NO_ID) { //If this view has no id
            id = View.generateViewId()
        }
    }

    private fun setupViewAsClickable() {
        isFocusable = true
        textContainer.isClickable = true
        textContainer.isFocusable = true
    }

    private fun setupEnabledView() {
        if(state == AndesTextfieldState.DISABLED || state == AndesTextfieldState.READONLY){
            isEnabled = false
            textComponent.isEnabled = isEnabled
            textContainer.isEnabled = isEnabled
            textfieldContainer.isEnabled = isEnabled
        } else {
            isEnabled = true
            textComponent.isEnabled = isEnabled
            textContainer.isEnabled = isEnabled
            textfieldContainer.isEnabled = isEnabled
        }
    }

    /**
     * Gets data from the config to sets the color of the components regarding to the state.
     *
     */
    private fun setupColorComponents(config: AndesTextfieldConfiguration) {
        textContainer.background = config.background

        iconComponent.setImageDrawable(config.icon)
        if (config.icon != null) {
            iconComponent.visibility = View.VISIBLE

        } else {
            iconComponent.visibility = View.GONE
        }

        helperComponent.setTextColor(config.helperColor.colorInt(context))
        helperComponent.typeface = config.helperTypeface

        labelComponent.setTextColor(config.labelColor.colorInt(context))
        labelComponent.typeface = config.typeface

        counterComponent.setTextColor(config.counterColor.colorInt(context))
        counterComponent.typeface = config.typeface

        textComponent.setHintTextColor(config.placeHolderColor.colorInt(context))
    }

    /**
     * Gets data from the config and sets to the Label component.
     *
     */
    private fun setupLabelComponent(config: AndesTextfieldConfiguration) {
        if (config.labelText == null || config.labelText.isEmpty()) {
            labelComponent.visibility = View.GONE
        } else {
            labelComponent.visibility = View.VISIBLE
            labelComponent.text = config.labelText
            labelComponent.setTextSize(TypedValue.COMPLEX_UNIT_PX, config.labelSize)
        }
    }

    /**
     * Gets data from the config and sets to the Helper component.
     *
     */
    private fun setupHelperComponent(config: AndesTextfieldConfiguration) {
        if (config.helperText == null || config.helperText.isEmpty()) {
            helperComponent.visibility = View.GONE
        } else {
            helperComponent.visibility = View.VISIBLE
            helperComponent.text = config.helperText
            helperComponent.setTextSize(TypedValue.COMPLEX_UNIT_PX, config.helperSize)
        }
    }

    /**
     * Gets data from the config and sets to the Counter component.
     *
     */
    private fun setupCounterComponent(config: AndesTextfieldConfiguration) {
        if (config.counterMaxLength!! <= config.counterMinLength!!) {
            counterComponent.visibility = View.GONE
        } else {
            counterComponent.visibility = View.VISIBLE
            counterComponent.setTextSize(TypedValue.COMPLEX_UNIT_PX, config.counterSize)
            counterComponent.text = resources.getString(R.string.andes_textfield_counter_text, config.counterMinLength,config.counterMaxLength)
            textComponent.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(config.counterMaxLength!!))
        }

        textComponent.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                counterComponent.text = resources.getString(R.string.andes_textfield_counter_text, count, config.counterMaxLength)
            }
        })
    }

    /**
     * Gets data from the config and sets to the Place Holder component.
     *
     */
    private fun setupPlaceHolderComponent(config: AndesTextfieldConfiguration) {
        if (config.placeHolderText != null) {
            textComponent.hint = config.placeHolderText
            textComponent.typeface = config.typeface
        }
    }

    private fun setupLeftComponent(config: AndesTextfieldConfiguration) {
        if (config.leftComponent != null) {
            leftComponent.removeAllViews()
            leftComponent.addView(config.leftComponent)

            val params = leftComponent.layoutParams as ConstraintLayout.LayoutParams
            params.marginStart = config.leftComponentLeftMargin!!
            params.marginEnd = config.leftComponentRightMargin!!
            leftComponent.layoutParams = params

            leftComponent.visibility = View.VISIBLE
        } else {
            leftComponent.visibility = View.GONE
        }
    }

    private fun setupRightComponent(config: AndesTextfieldConfiguration) {
        if (config.rightComponent != null) {
            rightComponent.removeAllViews()
            rightComponent.addView(config.rightComponent)
            setupClear()

            val params = rightComponent.layoutParams as ConstraintLayout.LayoutParams
            params.marginStart = config.rightComponentLeftMargin!!
            params.marginEnd = config.rightComponentRightMargin!!
            rightComponent.layoutParams = params

            rightComponent.visibility = View.VISIBLE

        } else {
            rightComponent.visibility = View.GONE
        }
    }

    private fun setupClear() {
        if (rightContent == AndesTextfieldRightContent.CLEAR) {
            val clear: SimpleDraweeView = rightComponent.getChildAt(0) as SimpleDraweeView
            clear.setOnClickListener { textComponent.text.clear() }
        }
    }

    fun setupAction(text: String, onClickListener: OnClickListener) {
        rightContent = AndesTextfieldRightContent.ACTION
        val action: AndesButton = rightComponent.getChildAt(0) as AndesButton
        action.text = text
        action.setOnClickListener(onClickListener)

    }

    fun setupRightIcon(iconPath: String) {
        rightContent = AndesTextfieldRightContent.ICON
        val rightIcon: SimpleDraweeView = rightComponent.getChildAt(0) as SimpleDraweeView
        rightIcon.setImageDrawable(buildColoredBitmapDrawable(
                OfflineIconProvider(context).loadIcon(iconPath) as BitmapDrawable,
                context,
                R.color.andes_gray_800.toAndesColor())
        )
    }

    fun setupLeftIcon(iconPath: String) {
        leftContent = AndesTextfieldLeftContent.ICON
        val leftIcon: SimpleDraweeView = leftComponent.getChildAt(0) as SimpleDraweeView
        leftIcon.setImageDrawable(buildColoredBitmapDrawable(
                OfflineIconProvider(context).loadIcon(iconPath) as BitmapDrawable,
                context,
                R.color.andes_gray_450.toAndesColor())
        )

    }

    fun setupPrefix(text: String) {
        leftContent = AndesTextfieldLeftContent.PREFIX
        val prefix: TextView = leftComponent.getChildAt(0) as TextView
        prefix.text = text
    }


    fun setupSuffix(text: String) {
        rightContent = AndesTextfieldRightContent.SUFFIX
            val suffix: TextView = leftComponent.getChildAt(0) as TextView
            suffix.text = text
    }

    private fun createConfig() = AndesTextfieldConfigurationFactory.create(context, andesTextfieldAttrs)
}