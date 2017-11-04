package com.gzlk.android.isp.fragment.base;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.helper.PreferenceHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.hlk.hlklib.lib.emoji.EmojiBoard;
import com.hlk.hlklib.lib.emoji.EmojiClipboardCatchableEditText;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.hlk.hlklib.lib.view.SoftInputLayout;
import com.hlk.hlklib.lib.voice.VoiceRecordButton;

/**
 * <b>功能描述：</b>提供文字输入<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/28 09:59 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/28 09:59 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseChatInputSupportFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_INPUT_TYPE = "bcisf_input_type";

    // 主控制容器，以此控制键盘和表情输入框等的显示
    @ViewId(R.id.ui_chatable_main_container)
    public SoftInputLayout _mainInputContainer;

    // 按钮
    @ViewId(R.id.ui_tool_chatable_inputbar_recorder)
    public CustomTextView _recorder;
    @ViewId(R.id.ui_tool_chatable_inputbar_emoji)
    public CustomTextView _emoji;
    @ViewId(R.id.ui_tool_chatable_inputbar_append)
    public CustomTextView _append;

    // 录音layout
    @ViewId(R.id.ui_tool_chatable_recorder_layout)
    public View _recorderContainer;
    @ViewId(R.id.ui_chat_bar_recorder)
    public VoiceRecordButton _inputVoiceRecorder;

    @ViewId(R.id.ui_tool_chatable_emoji_layout)
    public EmojiBoard _emojiContainer;

    @ViewId(R.id.ui_tool_chatable_inputbar_input)
    public EmojiClipboardCatchableEditText _inputText;
    @ViewId(R.id.ui_tool_chatable_inputbar_send)
    public CorneredButton _inputSend;
    @ViewId(R.id.ui_tool_chatable_inputbar_reply)
    public TextView replyName;

    // 普通文本输入方式
    public static final int TYPE_NORMAL = 0;
    // 录音输入方式
    public static final int TYPE_RECORD = 1;
    // 表情输入方式
    public static final int TYPE_EMOJI = 2;
    // 打开相机拍照
    public static final int TYPE_CAMERA = 3;
    // 打开相册选择图片
    public static final int TYPE_IMAGE = 4;
    // 键盘输入方式
    public static final int TYPE_KEYBOARD = 5;
    // Params
    private int inputType = TYPE_NORMAL;

    /**
     * 是否在发送按钮点击之后清空输入区
     */
    protected boolean clearInputWhenSend = true;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        inputType = bundle.getInt(PARAM_INPUT_TYPE, TYPE_NORMAL);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_INPUT_TYPE, inputType);
    }

    @Override
    public void doingInResume() {
        hideSendButton();
        // 根据之前保存的键盘高度设置表情区域的高度
        resetAdditionalBarHeight();
        initializeInputType();
    }

    private void hideSendButton() {
        String text = null == _inputText ? "" : _inputText.getText().toString();
        if (null != _inputSend) {
            _inputSend.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        }
    }

    protected void setSendText(int text) {
        if (null != _inputSend) {
            _inputSend.setText(text);
        }
    }

    protected void setSendText(String text) {
        if (null != _inputSend) {
            _inputSend.setText(text);
        }
    }

    /**
     * 当BackKey触发时，查看表情框是否显示，此时优先隐藏表情框
     */
    @Override
    protected boolean onBackKeyPressed() {
        if (!super.onBackKeyPressed()) {
            if (null != _mainInputContainer && _mainInputContainer.isEmojiLayoutShow()) {
                _mainInputContainer.hideEmojiLayout();
                resetAdditionalInputType(TYPE_NORMAL);
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    @Click({R.id.ui_tool_chatable_inputbar_recorder,
            R.id.ui_tool_chatable_inputbar_emoji,
            R.id.ui_tool_chatable_inputbar_append,
            R.id.ui_tool_chatable_inputbar_input,
            R.id.ui_tool_chatable_inputbar_send})
    public void inputElementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_tool_chatable_inputbar_recorder:
                if (inputType == TYPE_RECORD) {
                    resetAdditionalInputType(TYPE_KEYBOARD);
                } else {
                    _mainInputContainer.showEmojiLayout();
                    resetAdditionalInputType(TYPE_RECORD);
                }
                break;
            case R.id.ui_tool_chatable_inputbar_emoji:
                if (inputType == TYPE_EMOJI) {
                    _mainInputContainer.hideEmojiLayout();
                    resetAdditionalInputType(TYPE_NORMAL);
                } else {
                    _mainInputContainer.showEmojiLayout();
                    resetAdditionalInputType(TYPE_EMOJI);
                }
                break;
            case R.id.ui_tool_chatable_inputbar_append:
                startGalleryForResult();
                break;
            case R.id.ui_tool_chatable_inputbar_input:
                // 隐藏表情框
                resetAdditionalInputType(TYPE_KEYBOARD);
                break;
            case R.id.ui_tool_chatable_inputbar_send:
                String text = _inputText.getText().toString();
                if (null != _OnInputCompleteListener) {
                    _OnInputCompleteListener.onInputComplete(text, text.length(), 1);
                }
                clearTextInput();
                break;
        }
    }

    private void resetRecorderIcon() {
        if (null != _recorder) {
            _recorder.setText(inputType == TYPE_RECORD ? R.string.ui_icon_keyboard : R.string.ui_icon_recorder);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void resetAdditionalInputType(int toType) {
        inputType = toType;
        resetRecorderIcon();
        if (inputType == TYPE_KEYBOARD) {
            focusInputText(true);
            resetButtonState();
            return;
        }
        if (null != _recorderContainer) {
            _recorderContainer.setVisibility(inputType == TYPE_RECORD ? View.VISIBLE : View.INVISIBLE);
        }
        if (null != _emojiContainer) {
            _emojiContainer.setVisibility(inputType == TYPE_EMOJI ? View.VISIBLE : View.INVISIBLE);
        }
        if (inputType == TYPE_RECORD) {
            if (null != _inputVoiceRecorder) {
                _inputVoiceRecorder.setPath(App.app().getCachePath(App.VOICE_DIR));
            }
        }
        resetButtonState();
    }

    private void resetButtonState() {
        int color1 = getColor(R.color.colorPrimary);
        int color2 = getColor(R.color.textColor);
        if (null != _recorder) {
            _recorder.setTextColor(inputType == TYPE_RECORD ? color1 : color2);
        }
        if (null != _emoji) {
            _emoji.setTextColor(inputType == TYPE_EMOJI ? color1 : color2);
        }
    }

    /**
     * 给inputText设置焦点
     */
    protected void focusInputText(boolean openKeyboard) {
        if (null == _inputText) return;

        _inputText.setFocusable(true);
        _inputText.setFocusableInTouchMode(true);
        _inputText.requestFocus();
        int start = _inputText.getSelectionStart();
        _inputText.setSelection(start);
        if (openKeyboard) {
            openKeyboard();
        }
    }

    private void openKeyboard() {
        Handler().post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) _inputText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(_inputText, 0);
            }
        });
    }

    // 控制按钮
    protected boolean showRecorder = true;
    protected boolean showLink = true;
    protected boolean showVideo = true;
    protected boolean showCamera = true;
    protected boolean showPhoto = true;
    protected boolean showEmoji = true;
    protected boolean showMap = true;
    protected boolean showAppend = true;

    private void initializeInputType() {
        // 显示或隐藏按钮
        if (null != _recorder) {
            _recorder.setVisibility(showRecorder ? View.VISIBLE : View.GONE);
        }
        if (null != _emoji) {
            _emoji.setVisibility(showEmoji ? View.VISIBLE : View.GONE);
        }
        if (null != _append) {
            _append.setVisibility(showAppend ? View.VISIBLE : View.GONE);
        }
        // 设置键盘显示或隐藏时的回调控制
        if (null != _mainInputContainer) {
            _mainInputContainer.setOnSoftInputChangeListener(softInputChangeListener);

            if (null != _inputVoiceRecorder) {
                _inputVoiceRecorder.addOnRecordFinishedListener(mOnRecordFinishedListener);
            }

            _inputText.addTextChangedListener(_TextWatcher);
        } else {
            log("Cannot initialize multimedia input mode with null layout container.");
        }
        if (null != _emojiContainer) {
            _emojiContainer.setOnEmojiSelectedListener(onEmojiSelectedListener);
        }
    }

    private SoftInputLayout.OnSoftInputChangeListener softInputChangeListener = new SoftInputLayout.OnSoftInputChangeListener() {
        @Override
        public void onSoftInputChange(boolean show, int layoutHeight, int contentHeight) {
            if (show) {
                resetAdditionalInputType(TYPE_NORMAL);
            }
            if (layoutHeight != contentHeight) {
                if (0 == keyboardHeight) {
                    keyboardHeight = layoutHeight - contentHeight;
                    // 保存键盘高度以便下次再使用
                    PreferenceHelper.save(R.string.pf_static_keyboard_height, String.valueOf(keyboardHeight));
                    log(format("save keyboard height to %d", keyboardHeight));
                }
            }
            // 向子类对象传递键盘状态改变的事件
            if (null != mRecyclerView) {
                smoothScrollToBottom(mRecyclerView.getAdapter().getItemCount() - 1);
            }
//            if (null != __OnKeyboardStateChangeListener) {
//                __OnKeyboardStateChangeListener.onKeyboardStateChanged(show);
//            }
        }
    };

    private int keyboardHeight = 0;

    private void resetAdditionalBarHeight() {
        if (keyboardHeight == 0) {
            // 读取预存的键盘高度
            keyboardHeight = Integer.valueOf(PreferenceHelper.get(R.string.pf_static_keyboard_height, "0"));
            if (null != _mainInputContainer) {
                _mainInputContainer.setDefaultEmojiLayoutHeight(keyboardHeight);
            }
        }
    }

    private VoiceRecordButton.OnRecordFinishedListener mOnRecordFinishedListener = new VoiceRecordButton.OnRecordFinishedListener() {
        @Override
        public void onRecordFailed() {
            warningRecordFailed();
        }

        @Override
        public void onFinished(long length, String path) {
            //showProgressDialog();
            //uploadFile(path, (int) length, Chat.Type.Audio);
        }
    };

    private EmojiBoard.OnEmojiSelectedListener onEmojiSelectedListener = new EmojiBoard.OnEmojiSelectedListener() {
        @Override
        public void onSelected(SpannableString emoji) {
            //addSpannable(spannableString);
//            SpannableString ss = new SpannableString(emojiSpanText);
//            Drawable d = getResources().getDrawable(emojiResId);
//            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//            CenteredImageSpan span = new CenteredImageSpan(d, ImageSpan.ALIGN_BOTTOM);
//            ss.setSpan(span, 0, emojiSpanText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            addSpannable(emoji);
            // 当前焦点不在inputText上时将焦点转过来
            focusInputText(false);
        }
    };

    /**
     * 将指定的文本追加到文本框的光标所在处
     */
    private void addSpannable(SpannableString spannable) {
        if (null != _inputText) {
            boolean focused = _inputText.hasFocus();
            Editable editable = _inputText.getEditableText();
            int selectionStart = _inputText.getSelectionStart();
            int len = editable.length();
            if (!focused) {
                selectionStart = len;
            }
            if (selectionStart < 0 || selectionStart >= len) {
                editable.append(spannable);
            } else {
                editable.insert(selectionStart, spannable);
            }
            selectionStart += spannable.length();
            _inputText.setSelection(selectionStart);
        }
    }

    protected void clearTextInput() {
        _inputText.setText(null);
    }

    private TextWatcher _TextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            hideSendButton();
        }
    };

    // 无法录音，可能需要申请运行时权限
    private void warningRecordFailed() {
        String record = StringHelper.getString(R.string.ui_base_text_record_voice);
        String text = StringHelper.getString(R.string.ui_grant_permission_device, record, record);
        String denied = StringHelper.getString(R.string.ui_text_permission_voice_recorder_denied);
        tryGrantPermission(Manifest.permission.RECORD_AUDIO, GRANT_RECORD_AUDIO, text, denied);
    }

    @Override
    public void permissionGranted(String[] permissions, int requestCode) {
        if (requestCode == GRANT_RECORD_AUDIO) {
            ToastHelper.make().showMsg(R.string.ui_text_permission_voice_recorder_allowed);
        }
        super.permissionGranted(permissions, requestCode);
    }

    protected void setInputHint(String hint) {
        _inputText.setHint(hint);
    }

    protected void setInputHint(int hint) {
        _inputText.setHint(hint);
    }

    protected void setButtonText(String text) {
        _inputSend.setText(text);
    }

    protected void setButtonText(int text) {
        _inputSend.setText(text);
    }

    private OnInputCompleteListener _OnInputCompleteListener;

    /**
     * 添加文字输入完毕事件的处理回调
     */
    protected void addOnInputCompleteListener(OnInputCompleteListener l) {
        _OnInputCompleteListener = l;
    }

    /**
     * 文本输入完毕事件监听(点击发送按钮之后)
     */
    protected interface OnInputCompleteListener {
        /**
         * 文本输入完毕回调
         *
         * @param text 已输入的字符
         * @param type 输入类别，1=文本2=图片3=语音4=HTML
         */
        void onInputComplete(String text, int length, int type);
    }

    private OnKeyboardStateChangeListener __OnKeyboardStateChangeListener;

    /**
     * 添加键盘状态更改事件的处理回调
     */
    protected void addOnKeyboardStateChangeListener(OnKeyboardStateChangeListener l) {
        __OnKeyboardStateChangeListener = l;
    }

    /**
     * 键盘状态改变
     */
    protected interface OnKeyboardStateChangeListener {
        /**
         * 键盘状态改变回调
         *
         * @param shown 键盘是否显示
         */
        void onKeyboardStateChanged(boolean shown);
    }
}
