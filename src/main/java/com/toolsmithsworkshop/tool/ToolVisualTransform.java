package com.toolsmithsworkshop.tool;

public record ToolVisualTransform(float x, float y, float scale, float depthScale) {
    public static final ToolVisualTransform DEFAULT = new ToolVisualTransform(0.0f, 0.0f, 1.0f, 1.0f);
    public static final ToolVisualTransform HEAD = new ToolVisualTransform(0.0f, 0.0f, 1.0f, 1.4f);
    public static final ToolVisualTransform BINDING = new ToolVisualTransform(0.0f, 0.0f, 1.0f, 1.8f);
    public static final ToolVisualTransform SWORD_GRIP = new ToolVisualTransform(0.0f, 0.0625f, 1.0f, 1.0f);
}
