package com.longforus.mvpautocodeplus.ui;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.MutableCollectionComboBoxModel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.longforus.mvpautocodeplus.ConsKt;
import com.longforus.mvpautocodeplus.Utils;
import com.longforus.mvpautocodeplus.config.ItemConfigBean;
import com.longforus.mvpautocodeplus.config.PersistentState;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import org.apache.http.util.TextUtils;

public class EnterKeywordDialog extends JDialog {
    private static final String NAME_CHECK_STR = "[a-zA-Z]+[0-9a-zA-Z_]";
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField et_name;
    private JRadioButton mJavaRadioButton;
    private JRadioButton mKotlinRadioButton;
    private JRadioButton mActivityRadioButton;
    private JRadioButton mFragmentRadioButton;
    private JCheckBox mViewCheckBox;
    private JCheckBox mPresenterCheckBox;
    private JCheckBox mModelCheckBox;
    private JComboBox<String> cob_v;
    private JComboBox<String> cob_p;
    private JComboBox<String> cob_m;
    private OnOkListener onOkListener;

    private EnterKeywordDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> {
            onOK();
            buttonOK.setEnabled(false);
        });

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void main(String[] args) {
        getDialog(null);
        System.exit(0);
    }

    {
        // GUI initializer generated by IntelliJ IDEA GUI Designer
        // >>> IMPORTANT!! <<<
        // DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    public static EnterKeywordDialog getDialog(OnOkListener onOkListener) {
        PersistentState state = ServiceManager.getService(PersistentState.class);
        EnterKeywordDialog dialog = new EnterKeywordDialog();
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) screensize.getWidth() / 2 - dialog.getWidth() / 2;
        int y = (int) screensize.getHeight() / 2 - dialog.getHeight() / 2;
        dialog.setTitle("MvpAutoCodePlus");
        dialog.setLocation(x, y);
        dialog.onOkListener = onOkListener;
        String sv = state.getValue(ConsKt.SUPER_VIEW);
        String sp = state.getValue(ConsKt.SUPER_PRESENTER);
        String sm = state.getValue(ConsKt.SUPER_MODEL);
        if (TextUtils.isEmpty(sm) || TextUtils.isEmpty(sp) || TextUtils.isEmpty(sv)) {
            Messages.showErrorDialog("Super interface not set," + ConsKt.GOTO_SETTING, "Error");
            return null;
        } else {
            setSavedSuperClass(dialog, state);
            dialog.pack();
            dialog.setVisible(true);
        }
        return dialog;
    }

    private static void setSavedSuperClass(EnterKeywordDialog dialog, PersistentState state) {
        dialog.mActivityRadioButton.addChangeListener(e -> {
            if (dialog.mActivityRadioButton.isSelected()) {
                setSuperClass(dialog.cob_v, state.getValue(ConsKt.SUPER_VIEW_ACTIVITY), dialog.mViewCheckBox, ConsKt.IS_NOT_SET + "," + ConsKt.GOTO_SETTING);
            } else {
                setSuperClass(dialog.cob_v, state.getValue(ConsKt.SUPER_VIEW_FRAGMENT), dialog.mViewCheckBox, ConsKt.IS_NOT_SET + "," + ConsKt.GOTO_SETTING);
            }
        });
        setSuperClass(dialog.cob_v, state.getValue(ConsKt.SUPER_VIEW_ACTIVITY), dialog.mViewCheckBox, ConsKt.IS_NOT_SET + "," + ConsKt.GOTO_SETTING);
        setSuperClass(dialog.cob_p, state.getValue(ConsKt.SUPER_PRESENTER_IMPL), null, ConsKt.IS_NOT_SET + "," + ConsKt.NO_SUPER_CLASS);
        setSuperClass(dialog.cob_m, state.getValue(ConsKt.SUPER_MODEL_IMPL), null, ConsKt.IS_NOT_SET + "," + ConsKt.NO_SUPER_CLASS);
    }

    private static void setSuperClass(JComboBox<String> cob, String value, JCheckBox jcb, String nullShowStr) {
        if (TextUtils.isEmpty(value)) {
            value = nullShowStr;
            if (jcb != null) {
                jcb.setSelected(false);
            }
        } else if (jcb != null) {
            jcb.setSelected(true);
        }
        cob.setModel(new MutableCollectionComboBoxModel<String>(Arrays.asList(value.split(";"))));
        cob.setSelectedIndex(0);
    }

    private static String getSelectedContent(JComboBox<String> cob) {
        Object item = cob.getSelectedItem();
        if (item == null) {
            return "";
        } else {
            return (String) item;
        }
    }

    private void onOK() {
        String key = et_name.getText();
        if (Utils.isEmpty(key)) {
            Messages.showErrorDialog("Name not allow empty!", "Error");
            return;
        }
        if (!key.matches(NAME_CHECK_STR)) {
            Messages.showErrorDialog("An illegal name!", "Error");
            return;
        }
        if (checkImplementInValid(mViewCheckBox, cob_v, "View")) {
            return;
        }
        if (checkImplementInValid(mPresenterCheckBox, cob_p, "Presenter")) {
            return;
        }
        if (checkImplementInValid(mModelCheckBox, cob_m, "Model")) {
            return;
        }
        if (onOkListener != null) {
            onOkListener.onOk(new ItemConfigBean(key, mJavaRadioButton.isSelected(), mActivityRadioButton.isSelected(), getSelectedContent(cob_v), getSelectedContent(cob_p),
                getSelectedContent(cob_m)));
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private boolean checkImplementInValid(JCheckBox cb, JComboBox<String> cob, String item) {
        if (cb.isSelected()) {
            String value = (String) cob.getSelectedItem();
            if (TextUtils.isEmpty(value) || ConsKt.IS_NOT_SET.equals(value)) {
                Messages.showErrorDialog(item + " implement is invalid ", "Error");
                return true;
            }
        }
        return false;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new javax.swing.JPanel();
        contentPane.setLayout(new GridLayoutManager(6, 1, new java.awt.Insets(10, 10, 10, 10), -1, -1));
        final javax.swing.JPanel panel1 = new javax.swing.JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new java.awt.Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1,
            new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1,
            new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final javax.swing.JPanel panel2 = new javax.swing.JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new java.awt.Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2,
            new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new javax.swing.JButton();
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new javax.swing.JButton();
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final javax.swing.JPanel panel3 = new javax.swing.JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new java.awt.Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3,
            new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final javax.swing.JLabel label1 = new javax.swing.JLabel();
        panel3.add(label1,
            new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null,
                null, 0, false));
        et_name = new javax.swing.JTextField();
        panel3.add(et_name,
            new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                null, new java.awt.Dimension(150, -1), null, 0, false));
        final javax.swing.JPanel panel4 = new javax.swing.JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new java.awt.Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel4,
            new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Implement code type:"));
        mJavaRadioButton = new javax.swing.JRadioButton();
        panel4.add(mJavaRadioButton,
            new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mKotlinRadioButton = new javax.swing.JRadioButton();
        panel4.add(mKotlinRadioButton,
            new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final javax.swing.JPanel panel5 = new javax.swing.JPanel();
        panel5.setLayout(new java.awt.GridBagLayout());
        contentPane.add(panel5,
            new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Generate implement item:"));
        mViewCheckBox = new javax.swing.JCheckBox();
        java.awt.GridBagConstraints gbc;
        gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        panel5.add(mViewCheckBox, gbc);
        mPresenterCheckBox = new javax.swing.JCheckBox();
        gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        panel5.add(mPresenterCheckBox, gbc);
        mModelCheckBox = new javax.swing.JCheckBox();
        gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        panel5.add(mModelCheckBox, gbc);
        cob_v = new javax.swing.JComboBox();
        gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 20.0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panel5.add(cob_v, gbc);
        cob_p = new javax.swing.JComboBox();
        gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panel5.add(cob_p, gbc);
        cob_m = new javax.swing.JComboBox();
        gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panel5.add(cob_m, gbc);
        final javax.swing.JPanel panel6 = new javax.swing.JPanel();
        panel6.setLayout(new GridLayoutManager(1, 2, new java.awt.Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel6,
            new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel6.setBorder(javax.swing.BorderFactory.createTitledBorder("View implement type:"));
        mActivityRadioButton = new javax.swing.JRadioButton();
        panel6.add(mActivityRadioButton,
            new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mFragmentRadioButton = new javax.swing.JRadioButton();
        panel6.add(mFragmentRadioButton,
            new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        contentPane.add(spacer2,
            new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new java.awt.Dimension(-1, 50),
                null, null, 0, false));
        javax.swing.ButtonGroup buttonGroup;
        buttonGroup = new javax.swing.ButtonGroup();
        buttonGroup.add(mJavaRadioButton);
        buttonGroup.add(mKotlinRadioButton);
        buttonGroup = new javax.swing.ButtonGroup();
        buttonGroup.add(mActivityRadioButton);
        buttonGroup.add(mFragmentRadioButton);
    }

    /** @noinspection ALL */
    public javax.swing.JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    @FunctionalInterface
    public interface OnOkListener {
        void onOk(ItemConfigBean str);
    }
}
