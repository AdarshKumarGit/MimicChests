package org.chubby.github.mmimicchests.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.chubby.github.mmimicchests.Config;
import org.chubby.github.mmimicchests.Constants;

public class ModConfigScreen extends Screen {

    private final Screen parentScreen;
    private EditBox mimicChanceField;
    private double mimicChanceValue;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int FIELD_WIDTH = 150;
    private static final int FIELD_HEIGHT = 20;
    private static final Component TITLE = Component.translatable("config." + Constants
            .MOD_ID + ".title");

    public ModConfigScreen(Screen parentScreen) {
        super(TITLE);
        this.parentScreen = parentScreen;
        this.mimicChanceValue = Config.COMMON.mimicChestSpawnChance.get();
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        Component mimicLabel = Component.translatable("config." + Constants.MOD_ID + ".mimic_chance");
        int labelWidth = this.font.width(mimicLabel);

        this.mimicChanceField = new EditBox(
                this.font,
                centerX - FIELD_WIDTH / 2,
                centerY - 20,
                FIELD_WIDTH,
                FIELD_HEIGHT,
                Component.empty()
        );
        mimicChanceField.setValue(String.valueOf(mimicChanceValue));
        mimicChanceField.setFilter(this::validateNumberInput);
        this.addRenderableWidget(mimicChanceField);

        this.addRenderableWidget(Button.builder(
                        Component.translatable("config." + Constants.MOD_ID + ".save"),
                        button -> {
                            saveSettings();
                            this.minecraft.setScreen(parentScreen);
                        })
                .pos(centerX - BUTTON_WIDTH - 5, centerY + 40)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build()
        );

        this.addRenderableWidget(Button.builder(
                        Component.translatable("config." + Constants.MOD_ID + ".cancel"),
                        button -> this.minecraft.setScreen(parentScreen))
                .pos(centerX + 5, centerY + 40)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build()
        );
    }

    @Override
    public void render(GuiGraphics poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;

        Component mimicLabel = Component.translatable("config." + Constants.MOD_ID + ".mimic_chance");
       poseStack.drawString(font, mimicLabel, centerX - FIELD_WIDTH / 2, this.height / 2 - 35, 0xFFFFFF);

        Component description = Component.translatable("config." + Constants.MOD_ID + ".mimic_chance.description");
        poseStack.drawString(font, description, centerX - FIELD_WIDTH / 2, this.height / 2 + 10, 0xAAAAAA);
    }

    private boolean validateNumberInput(String input) {
        if (input.isEmpty()) {
            return true;
        }

        try {
            double value = Double.parseDouble(input);
            return value >= 0.0 && value <= 1.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void saveSettings() {
        try {
            double newChance = Double.parseDouble(mimicChanceField.getValue());

            newChance = Math.max(0.0, Math.min(1.0, newChance));

            Config.COMMON.mimicChestSpawnChance.set(newChance);

            Config.COMMON_CONFIG.save();
        } catch (Exception e) {
            mimicChanceField.setValue(String.valueOf(Config.COMMON.mimicChestSpawnChance.get()));
        }
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}
