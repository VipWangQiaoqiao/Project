package net.oschina.app.improve.detail.sign;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.SignUpEventOptions;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.common.widget.FlowLayout;

import java.util.ArrayList;

/**
 * Created by haibin
 * on 2016/12/7.
 */

final class ViewFactory {
    static View createView(Activity activity, LayoutInflater inflater, final SignUpEventOptions options) {
        View view = null;
        switch (options.getFormType()) {
            case SignUpEventOptions.FORM_TYPE_TEXT:
                view = getEditTextView(activity, inflater, options, InputType.TYPE_CLASS_TEXT);
                break;
            case SignUpEventOptions.FORM_TYPE_TEXT_AREA:
                //view = getEditTextArea(activity, inflater, options);
                break;
            case SignUpEventOptions.FORM_TYPE_SELECT:
                view = getSelect(activity, inflater, options);
                break;
            case SignUpEventOptions.FORM_TYPE_CHECK_BOX:
                view = getCheckBox(activity, inflater, options);
                break;
            case SignUpEventOptions.FORM_TYPE_RADIO:
                view = getRadios(activity, inflater, options);
                break;
            case SignUpEventOptions.FORM_TYPE_EMAIL:
                view = getEditTextView(activity, inflater, options, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case SignUpEventOptions.FORM_TYPE_DATE:
                break;
            case SignUpEventOptions.FORM_TYPE_MOBILE:
                view = getEditTextView(activity, inflater, options, InputType.TYPE_CLASS_PHONE);
                break;
            case SignUpEventOptions.FORM_TYPE_NUMBER:
                view = getEditTextView(activity, inflater, options, InputType.TYPE_CLASS_NUMBER);
                break;
            case SignUpEventOptions.FORM_TYPE_URL:
                view = getEditTextView(activity, inflater, options, InputType.TYPE_TEXT_VARIATION_URI);
                break;
            default:
                break;
        }
        return view;
    }

    /**
     * 单行输入
     */
    private static View getEditTextView(Activity activity, LayoutInflater inflater, final SignUpEventOptions options, int inputType) {
        View view = inflater.inflate(R.layout.event_sign_up_edit_text, null);
        ((TextView) view.findViewById(R.id.tv_label)).setText(options.getLabel() + (options.isRequired() ? "" : "（选填）") + ":");
        EditText editText = (EditText) view.findViewById(R.id.et_value);
        editText.setText(options.getDefaultValue());
        options.setValue(options.getDefaultValue());
        editText.setInputType(inputType);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                options.setValue(s.toString());
            }
        });
        return view;
    }

    /**
     * 多行输入的V
     */
    private static View getEditTextArea(Activity activity, LayoutInflater inflater, final SignUpEventOptions options) {
        View view = inflater.inflate(R.layout.event_sign_up_edit_text_area, null);
        ((TextView) view.findViewById(R.id.tv_area)).setText(options.getLabel() + (options.isRequired() ? "" : "（选填）") + ":");
        EditText editText = (EditText) view.findViewById(R.id.et_area);
        editText.setText(options.getDefaultValue());
        options.setValue(options.getDefaultValue());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                options.setValue(s.toString());
            }
        });
        return view;
    }


    private static View getRadios(Activity activity, LayoutInflater inflater, final SignUpEventOptions options) {
        View view = inflater.inflate(R.layout.event_sign_up_radios, null);
        ((TextView) view.findViewById(R.id.tv_label)).setText(options.getLabel() + (options.isRequired() ? "" : "（选填）") + ":");
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rg_options);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMarginEnd(100);
        if (!TextUtils.isEmpty(options.getOption())) {
            String[] list = options.getOption().split(";");
            String[] status = null;
            if (!TextUtils.isEmpty(options.getOptionStatus()))
                status = options.getOptionStatus().split(";");
            for (int i = 0; i < list.length; i++) {
                RadioButton button = new RadioButton(activity);
                button.setLayoutParams(params);
                button.setText(list[i]);
                if (!TextUtils.isEmpty(options.getDefaultValue())) {
                    button.setChecked(list[0].equals(options.getDefaultValue()));
                    options.setValue(options.getDefaultValue());
                } else {
                    button.setChecked(i == 0);
                    options.setValue(list[0]);
                }
                boolean enable;
                if (status == null)
                    enable = true;
                else if (status.length <= i)
                    enable = true;
                else
                    enable = "0".equals(status[0]);
                button.setId(i);
                button.setEnabled(enable);
                radioGroup.addView(button);
            }
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String s[] = options.getOption().split(";");
                if (s != null)
                    options.setValue(s[checkedId]);
            }
        });
        return view;
    }

    private static View getCheckBox(Activity activity, LayoutInflater inflater, final SignUpEventOptions options) {
        View view = inflater.inflate(R.layout.event_sign_up_check_box, null);
        FlowLayout ll_check_box = (FlowLayout) view.findViewById(R.id.fl_check_box);
        ((TextView) view.findViewById(R.id.tv_label)).setText(options.getLabel() + (options.isRequired() ? "" : "（选填）") + ":");
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMarginEnd(100);
        if (options.getSelectList() == null)
            options.setSelectList(new ArrayList<String>());
        if (!TextUtils.isEmpty(options.getOption())) {
            final String[] list = options.getOption().split(";");
            String[] status = null;
            if (!TextUtils.isEmpty(options.getOptionStatus()))
                status = options.getOptionStatus().split(";");
            for (int i = 0; i < list.length; i++) {
                final CheckBox button = new CheckBox(activity);
                button.setLayoutParams(params);
                button.setText(list[i]);
                boolean enable;
                if (status == null)
                    enable = true;
                else if (status.length <= i)
                    enable = true;
                else
                    enable = "0".equals(status[i]);
                button.setId(i);
                button.setEnabled(enable);
                button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String item = list[button.getId()];
                        if (isChecked) {
                            options.getSelectList().add(item);
                        } else {
                            options.getSelectList().remove(item);
                        }
                    }
                });
                ll_check_box.addView(button);
            }
        }
        return view;
    }

    private static View getSelect(Activity activity, LayoutInflater inflater, final SignUpEventOptions options) {
        View view = inflater.inflate(R.layout.event_sign_up_select, null);
        ((TextView) view.findViewById(R.id.tv_label)).setText(options.getLabel() + (options.isRequired() ? "" : "（选填）") + ":");
        final TextView tv_select = (TextView) view.findViewById(R.id.tv_select);
        tv_select.setText(options.getDefaultValue());
        RecyclerView rv_select = (RecyclerView) inflater.inflate(R.layout.event_sign_up_select_list, null);
        final StringAdapter adapter = new StringAdapter(activity);
        rv_select.setLayoutManager(new LinearLayoutManager(activity));
        rv_select.setAdapter(adapter);
        if (options.getOption() != null) {
            String[] status = null;
            if (!TextUtils.isEmpty(options.getOptionStatus()))
                status = options.getOptionStatus().split(";");
            String[] list = options.getOption().split(";");
            for (int i = 0; i < list.length; i++) {
                StringAdapter.Select s = new StringAdapter.Select();
                s.setLabel(list[i]);
                boolean enable = false;
                if (status == null)
                    enable = true;
                else if (status.length <= i)
                    enable = true;
                else
                    enable = "0".equals(status[i]);
                s.setEnable(enable);
                adapter.addItem(s);
            }
        }


        final AlertDialog dialog = DialogHelper.getSelectDialog(activity, rv_select, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();

        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                StringAdapter.Select s = adapter.getItem(position);
                if (s.isEnable()) {
                    tv_select.setText(s.getLabel());
                    options.setValue(s.getLabel());
                    dialog.dismiss();
                }
            }
        });

        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_select);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        return view;
    }
}
