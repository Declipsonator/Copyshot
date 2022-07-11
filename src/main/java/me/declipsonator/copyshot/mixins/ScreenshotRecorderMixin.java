package me.declipsonator.copyshot.mixins;


import me.declipsonator.copyshot.util.ClipboardImage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;


@Mixin(ScreenshotRecorder.class)
public abstract class ScreenshotRecorderMixin {
    @Inject(method = "method_1661", at = @At("TAIL"))
    private static void onScreenshot(NativeImage image, File file, Consumer<Text> messageReceiver, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        try {
            File path = new File(mc.runDirectory.getAbsolutePath(), "screenshots");
            Optional<Path> lastFilePath = Files.list(path.toPath())
                    .filter(f -> !Files.isDirectory(f))
                    .max(Comparator.comparingLong(f -> f.toFile().lastModified()));
            Image lastScreen = new ImageIcon(lastFilePath.get().toString()).getImage();
            ClipboardImage newImage = new ClipboardImage(lastScreen);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(newImage, null);

            mc.inGameHud.getChatHud().addMessage(Text.of("Successfully copied screenshot to clipboard."));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}



