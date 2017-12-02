package com.leadcom.android.isp.crash;

import android.content.Context;

import com.leadcom.android.isp.BuildConfig;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.task.AsyncExecutableTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/01 19:48 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/01 19:48 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

class CrashSender extends AsyncExecutableTask<String, Void, Void> {

    private SoftReference<Context> context;

    CrashSender(Context context) {
        this.context = new SoftReference<>(context);
    }

    /**
     * 当前邮件发送对象
     */
    private EmailSender sender;

    /**
     * 将收集到的信息发送邮件
     */
    private void postReport(String content) throws MessagingException {
        String title = emailTitle(false);
        sendEmail(title, content);
    }

    /**
     * 获取email的标题
     */
    @SuppressWarnings("ConstantConditions")
    private String emailTitle(boolean cached) {
        String version = App.app().version();
        String name = StringHelper.getString(R.string.app_name_default);
        String ver = StringHelper.getString(R.string.app_internal_version);
        return StringHelper.format("%s(%s, %s(%s))", name, (cached ? "cached" : "direct"), version, (BuildConfig.DEBUG ? "Debug" : ver));
    }

    private boolean wbs = true;
    private String mailHost = wbs ? "www.wanbangsoftware.com" : "smtp.163.com";
    private String mailAccount = wbs ? "hsiang.leekwok" : "hlk_android@163.com";
    private String mailPassword = wbs ? "xlg_110004" : "android123456";
    private String mailSender = wbs ? "hsiang.leekwok@wanbangsoftware.com" : "hlk_android@163.com";
    //private String mailReceiver163 = "hlk_android@163.com";
    //private String mailReceiverAli = "an@51baigong.com";

    /**
     * 初始化邮件发送
     */
    private void initializeMailSender(String title, String content, List<String> receivers) throws MessagingException {
        sender = new EmailSender();
        // 设置服务器地址和端口
        sender.setProperties(mailHost, "25");
        // 分别设置发件人，邮件标题和文本内容
        sender.setMessage(mailSender, title, content);
        // 设置收件人
        sender.setReceiver(receivers);
    }

    /**
     * 发送邮件
     */
    private void sendEmail(String title, String content) throws MessagingException {
        List<String> receivers = new ArrayList<>();
        receivers.add(mailSender);
        initializeMailSender(title, content, receivers);
        // 发送邮件
        sender.sendEmail(mailAccount, mailPassword);
    }

    @Override
    protected Void doInTask(String... params) {
        try {
            if (null != context) {
                String exception = params[0];
                String emailContent = readFileFromRaw(context.get(), R.raw.email_content);
                String content = emailContent
                        // 机器参数列表
                        //.replace("%device_paramenters%", crash.toString())
                        // 堆栈调用信息
                        .replace("%stack_trace%", exception);
                // 日志
                //.replace("%logcat%", debug.toString());
                postReport(content);
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        return null;
    }

    private String readFileFromRaw(Context context, int rawFileId) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream myFile = context.getResources().openRawResource(rawFileId);
            BufferedReader br = new BufferedReader(new InputStreamReader(myFile, "gb2312"));
            String tmp;
            while ((tmp = br.readLine()) != null) {
                sb.append(tmp);
            }
            br.close();
            myFile.close();
        } catch (Exception ignore) {
        }
        return sb.toString();
    }
}
