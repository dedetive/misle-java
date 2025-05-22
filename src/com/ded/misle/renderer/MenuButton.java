package com.ded.misle.renderer;

import com.ded.misle.game.GamePanel;
import com.ded.misle.core.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

import static com.ded.misle.core.LanguageManager.getCurrentScript;
import static com.ded.misle.core.PraspomiaNumberConverter.ConvertMode.TO_PRASPOMIA;
import static com.ded.misle.core.PraspomiaNumberConverter.impureConvertNumberSystem;
import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.MainRenderer.textShadow;
import static com.ded.misle.renderer.FontManager.buttonFont;
import static com.ded.misle.renderer.SettingsMenuRenderer.settingState;

public class MenuButton {
    public enum MenuButtonID {
        MAIN_MENU_PLAY,
        MAIN_MENU_QUIT,
        MAIN_MENU_SETTINGS,
        MAIN_MENU_LEVEL_DESIGNER,

        PAUSE_MENU_QUIT,
        PAUSE_MENU_RESUME,
        PAUSE_MENU_SETTINGS,

        SETTINGS_MENU_GO_BACK,
        SETTINGS_TAB_EMPTY,
        SETTINGS_TAB_GENERAL,
        SETTINGS_TAB_GRAPHICS,
        SETTINGS_TAB_AUDIO,
        SETTINGS_TAB_GAMEPLAY,

        SETTING_LANGUAGE,
        SETTING_LANGUAGE_VALUE,
        SETTING_FULLSCREEN,
        SETTING_FULLSCREEN_VALUE,
        SETTING_FULLSCREEN_MODE,
        SETTING_FULLSCREEN_MODE_VALUE,
        SETTING_FRAME_RATE_CAP,
        SETTING_FRAME_RATE_CAP_VALUE,
        SETTING_DISPLAY_FPS,
        SETTING_DISPLAY_FPS_VALUE,
        SETTING_SCREEN_SIZE,
        SETTING_SCREEN_SIZE_VALUE,
        SETTING_ANTIALIASING,
        SETTING_ANTIALIASING_VALUE,
        SETTING_DISPLAY_MORE_INFO,
        SETTING_DISPLAY_MORE_INFO_VALUE,

        SAVE_SELECTOR_MENU_GO_BACK,
        SAVE_SELECTOR_SELECT_SAVE1,
        SAVE_SELECTOR_DELETE_SAVE1,
        SAVE_SELECTOR_SELECT_SAVE2,
        SAVE_SELECTOR_DELETE_SAVE2,
        SAVE_SELECTOR_SELECT_SAVE3,
        SAVE_SELECTOR_DELETE_SAVE3,
        SAVE_SELECTOR_CANCEL_DELETE_SAVE,
        SAVE_SELECTOR_CONFIRM_DELETE_SAVE,

        SAVE_CREATOR_MENU_GO_BACK,
        SAVE_CREATOR_MENU_ADD_ICON,
        SAVE_CREATOR_MENU_CLEAR_ICON,
        SAVE_CREATOR_MENU_CONFIRM_NAME,
    }


    Rectangle bounds;
    Color color;
    Runnable action;
    boolean isHovered;
    String originalText;
    String displayText;
    MenuButtonID id;
    boolean needsToUpdate;
    boolean functionEnabled = true;
    boolean renderEnabled = true;

    public static final Map<MenuButtonID, Fader.FadingState> fadingState = new EnumMap<>(MenuButtonID.class);
    public static final Map<MenuButtonID, Float> fadingProgress = new EnumMap<>(MenuButtonID.class);

    private static final List<MenuButton> buttons = Collections.synchronizedList(new ArrayList<>());

    public MenuButton(Rectangle bounds, Color defaultColor, Runnable action, String text, MenuButtonID id) {
        this.bounds = bounds;
        this.color = defaultColor;
        this.action = action;
        this.originalText = text;
        this.displayText = text;
        this.id = id;
        this.isHovered = false;
        this.needsToUpdate = true;
    }

    private static final Set<JPanel> initializedPanels = new HashSet<>();
    public static MenuButton createButton(Rectangle bounds, String text, Runnable action, JPanel panel, MenuButtonID id) {
        for (MenuButton button : buttons) {
            if (button.id == id && !button.needsToUpdate) {
                return button;
            }
        }

        MenuButton button = new MenuButton(bounds, buttonDefaultColor, action, text, id);
        buttons.add(button);

        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mouseLocation, panel);
        detectIfButtonHovered(mouseLocation, panel);

        if (!initializedPanels.contains(panel)) {
            initializedPanels.add(panel);

            panel.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (!button.renderEnabled) return;
                    detectIfButtonHovered(e.getPoint(), panel);
                }
            });

            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!button.functionEnabled) return;
                    Point clickPoint = e.getPoint();
                    clickPoint = new Point((int) (clickPoint.x / getWindowScale()), (int) (clickPoint.y / getWindowScale()));
                    for (MenuButton button : new ArrayList<>(buttons)) {
                        if (button.bounds.contains(clickPoint)) {
                            fadingState.put(button.id, Fader.FadingState.FADING_OUT);
                            fadingProgress.put(button.id, 0.75F);
                            button.action.run();
                            panel.setCursor(Cursor.getDefaultCursor());
                            clearButtons();
                            break;
                        }
                    }
                }
            });
        }

        button.needsToUpdate = false;
        return button;
    }

    private static void detectIfButtonHovered(Point mousePoint, JPanel panel) {
        mousePoint = new Point((int) (mousePoint.x / getWindowScale()), (int) (mousePoint.y / getWindowScale()));

        for (MenuButton button : buttons) {
            if (gameState == GamePanel.GameState.OPTIONS_MENU) {
                if (Objects.equals(button.originalText, LanguageManager.getText("settings_menu_" + settingState.name().toLowerCase()))) {
                    button.color = buttonCurrentMenu;
                    button.needsToUpdate = true;
                }
            }

            if (button.bounds.contains(mousePoint)) {
                if (!button.isHovered) {
                    button.isHovered = true;
                    button.color = buttonHoveredColor;
                    button.updateDisplayText();
                    button.needsToUpdate = true;
                    panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            } else if (button.isHovered) {
                button.isHovered = false;
                button.color = buttonDefaultColor;
                panel.setCursor(Cursor.getDefaultCursor());
                button.needsToUpdate = true;
            }
        }
    }

    private static final Map<MenuButtonID, String /* Color RGB */> textColors = new HashMap<>() {{
        put(MenuButtonID.MAIN_MENU_QUIT, "#FF7070");
        put(MenuButtonID.PAUSE_MENU_QUIT, "#FF7070");
        put(MenuButtonID.SETTINGS_MENU_GO_BACK, "#FF7070");
    }};

    private void updateDisplayText() {
        if (textColors.containsKey(this.id)) {
            this.displayText = "c{" + textColors.get(this.id) + "," + LanguageManager.getText(this.originalText) + "}";
        } else {
            this.displayText = this.originalText;
        }
    }

    public static void drawButtons(Graphics2D g2d) {
        try {
            for (MenuButton button : buttons) {
                if (!button.renderEnabled) return;

                g2d.setColor(buttonBorderColor);
                g2d.fillRoundRect(button.bounds.x - 1, button.bounds.y - 1,
                    button.bounds.width + 2, button.bounds.height + 2,
                    20, 20);

                g2d.setColor(button.color);
                g2d.fillRoundRect(button.bounds.x, button.bounds.y, button.bounds.width, button.bounds.height,
                    20, 20);

                g2d.setFont(buttonFont);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth;
                if (getCurrentScript() == LanguageManager.Script.PRASPOMIC) {
                    textWidth = fm.stringWidth(removeColorIndicators(impureConvertNumberSystem(button.displayText, TO_PRASPOMIA)));
                } else {
                    textWidth = fm.stringWidth(removeColorIndicators(button.displayText));
                }

                int textHeight = fm.getAscent();
                int textX = button.bounds.x + (button.bounds.width - textWidth) / 2;
                int textY = button.bounds.y + (button.bounds.height + textHeight) / 2 - fm.getDescent() + 2;

                // Shadows
                g2d.setColor(buttonTextShadowColor);
                drawColoredText(g2d, button.displayText, textX - textShadow, textY, g2d.getFont(), buttonTextShadowColor, true);
                drawColoredText(g2d, button.displayText, textX + textShadow, textY, g2d.getFont(), buttonTextShadowColor, true);
                drawColoredText(g2d, button.displayText, textX, textY - textShadow, g2d.getFont(), buttonTextShadowColor, true);
                drawColoredText(g2d, button.displayText, textX, textY + textShadow, g2d.getFont(), buttonTextShadowColor, true);

                // Main text
                drawColoredText(g2d, button.displayText, textX, textY, g2d.getFont(), buttonTextColor, false);

                // Fading effect
                if (fadingState.containsKey(button.id)) {
                    float progress = fadingProgress.get(button.id);
                    Color fadingColor = new Color(
                        buttonFadingColor.getRed() / 255f,
                        buttonFadingColor.getGreen() / 255f,
                        buttonFadingColor.getBlue() / 255f,
                        progress
                    );
                    g2d.setColor(fadingColor);
                    g2d.fillRoundRect(button.bounds.x, button.bounds.y, button.bounds.width, button.bounds.height, 20, 20);

                    fadingProgress.put(button.id, (float) Math.max(progress - 0.015f * deltaTime * 100, 0));
                    if (progress <= 0) {
                        fadingState.put(button.id, Fader.FadingState.UNFADED);
                    }
                }
            }
        } catch (ConcurrentModificationException e) {
            clearButtons();
        }
    }

    public static void createGoBackButton(JPanel panel, MenuButtonID id) {
        Rectangle button = new Rectangle(356, 220, 109, 31);
        createButton(button, LanguageManager.getText("settings_menu_go_back"), MenuRenderer::goToPreviousMenu, panel, id);
    }

    public static boolean exists(MenuButtonID id) {
        return buttons.stream().anyMatch(button -> button.id.equals(id));
    }

    public static void clearButtons() {
        for (MenuButton button : buttons) {
            button.setFunctionEnabled(false);
        }

        try {
            SwingUtilities.invokeAndWait(MenuButton::clear);
        } catch (InterruptedException | InvocationTargetException | Error e) {
            clear();
        }
    }

    private static void clear() {
        initializedPanels.clear();
        buttons.clear();
        clearButtonFading();
    }

    public static void clearButtonFading() {
        fadingProgress.clear();
        fadingState.clear();
    }

    public void setFunctionEnabled(boolean functionEnabled) {
        this.functionEnabled = functionEnabled;
    }
    public void setRenderEnabled(boolean renderEnabled) {
        this.renderEnabled = renderEnabled;
    }
}