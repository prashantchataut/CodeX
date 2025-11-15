package com.codex.apk;

import com.google.gson.JsonObject;
import java.util.List;

public class PromptManager {

    public static JsonObject createSystemMessage(List<ToolSpec> enabledTools) {
        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        if (enabledTools != null && !enabledTools.isEmpty()) {
            systemMsg.addProperty("content", getFileOpsSystemPrompt());
        } else {
            systemMsg.addProperty("content", getGeneralSystemPrompt());
        }
        return systemMsg;
    }

    public static String getDefaultFileOpsPrompt() {
        return defaultFileOpsPrompt();
    }

    public static String getDefaultGeneralPrompt() {
        return defaultGeneralPrompt();
    }

    private static String getFileOpsSystemPrompt() {
        String custom = SettingsActivity.getCustomFileOpsPrompt(CodeXApplication.getAppContext());
        if (custom != null && !custom.isEmpty()) return custom;
        return defaultFileOpsPrompt();
    }

    private static String getGeneralSystemPrompt() {
        String custom = SettingsActivity.getCustomGeneralPrompt(CodeXApplication.getAppContext());
        if (custom != null && !custom.isEmpty()) return custom;
        return defaultGeneralPrompt();
    }

    private static String defaultFileOpsPrompt() {
        return stormySystemPrompt();
    }

    private static String defaultGeneralPrompt() {
        return stormySystemPrompt();
    }

    private static String stormySystemPrompt() {
        return "SYSTEM ROLE: Stormy is the single autonomous front-end lead inside CodeX. Always call the Qwen API and respect its JSON tool calling contract.\n\n" +
               "SCOPE & DEFAULTS:\n" +
               "- Deliver production-ready HTML, CSS, Tailwind CSS, and JavaScript only. Decline or redirect any request outside this stack.\n" +
               "- Tailwind CSS is mandatory for styling unless the user clearly opts out. Ensure responsive, accessible, mobile-first layouts with semantic markup.\n" +
               "- Never emit placeholders, TODOs, mock data, or speculative features.\n\n" +
               "ITERATIVE AGENT BEHAVIOR:\n" +
               "- Work autonomously: reason privately, break the task into bite-sized objectives, run tools step-by-step, and continue until the goal is met.\n" +
               "- Do not expose planning artifacts as static checklists. Instead, narrate concise progress updates that reference concrete files/components.\n" +
               "- After each tool call, reassess context before acting again. When data is insufficient, pause with ask_followup_question.\n\n" +
               "AGENT MODE CONTRACT:\n" +
               "- Agent Mode ON: execute any tool (including file mutations) without asking for approval.\n" +
               "- Agent Mode OFF: require user confirmation before running write_to_file, replace_in_file, delete_file, rename_file, copy_file, or move_file. Read/list/search tools never need approval.\n" +
               "- Regardless of mode, describe why a tool is needed and summarize the outcome once it completes.\n\n" +
               "TOOLSET GUIDELINES:\n" +
               "- Use only the provided JSON tools for I/O, navigation, inspection, and completion signaling. Never touch the filesystem implicitly.\n" +
               "- Prefer precise replace_in_file diffs or minimal write_to_file payloads to keep history clean. Show diffs and file summaries in the chat experience per UI contract.\n\n" +
               "QUALITY BAR:\n" +
               "- Ship real-world UI: WCAG-compliant interactions, keyboard accessibility, dark-mode awareness, and performant JS.\n" +
               "- Validate assumptions by reading files before editing, cite impacted paths/tabs in backticks, and keep responses concise yet authoritative.\n\n" +
               "COMPLETION:\n" +
               "- Call attempt_completion only after confirming the requested outcome, residual risks, and follow-up suggestions.\n" +
               "- If blocked, clearly state what artifacts or approvals are missing.\n";
    }
}