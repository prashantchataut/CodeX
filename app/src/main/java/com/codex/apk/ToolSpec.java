package com.codex.apk;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Represents an OpenAI / Qwen compatible function tool specification that can
 * be transmitted via the "tools" array in the request body.  Only the minimal
 * properties required by Qwen are included.
 */
public class ToolSpec {

    private final String name;
    private final String description;
    private final JsonObject parametersSchema;

    public ToolSpec(String name, String description, JsonObject parametersSchema) {
        this.name = name;
        this.description = description;
        this.parametersSchema = parametersSchema;
    }

    public String getName() {
        return name;
    }

    /**
     * Serialises this ToolSpec into the expected JSON structure used by Qwen / OpenAI.
     */
    public JsonObject toJson() {
        JsonObject fn = new JsonObject();
        fn.addProperty("name", name);
        fn.addProperty("description", description);
        fn.add("parameters", parametersSchema);

        JsonObject wrapper = new JsonObject();
        wrapper.addProperty("type", "function");
        wrapper.add("function", fn);
        return wrapper;
    }

    /**
     * Converts a list of ToolSpec to a JsonArray ready for inclusion in the
     * request body.
     */
    public static JsonArray toJsonArray(java.util.List<ToolSpec> specs) {
        JsonArray arr = new JsonArray();
        for (ToolSpec spec : specs) {
            arr.add(spec.toJson());
        }
        return arr;
    }

    /* ------------------------------------------------------------
     * Convenience helpers: common file-system manipulation tools
     * ------------------------------------------------------------ */
    public static java.util.List<ToolSpec> stormyAgentTools() {
        java.util.List<ToolSpec> tools = new java.util.ArrayList<>();

        tools.add(new ToolSpec(
                "write_to_file",
                "Create or overwrite a file with the provided UTF-8 content. Creates parent directories if needed.",
                buildSchema(
                        new String[]{"path", "content"},
                        new String[]{"string", "string"},
                        new String[]{"Relative path to the file", "Full file contents"}
                )));

        tools.add(new ToolSpec(
                "replace_in_file",
                "Perform a targeted replacement inside a file using a SEARCH/=======/REPLACE diff block.",
                buildSchema(
                        new String[]{"path", "diff"},
                        new String[]{"string", "string"},
                        new String[]{"Relative path to the file", "Diff block formatted as SEARCH\\n=======\\nREPLACE"}
                )));

        tools.add(new ToolSpec(
                "read_file",
                "Read the entire contents of a file (truncated for very large files).",
                buildSchema(
                        new String[]{"path"},
                        new String[]{"string"},
                        new String[]{"Relative path to the file"}
                )));

        tools.add(new ToolSpec(
                "list_files",
                "List files and folders inside a directory with optional recursion.",
                buildSchema(
                        new String[]{"path", "recursive"},
                        new String[]{"string", "boolean"},
                        new String[]{"Relative directory path (use '.' for project root)", "Whether to recurse"}
                )));

        tools.add(new ToolSpec(
                "rename_file",
                "Rename or move a file/folder within the project workspace.",
                buildSchema(
                        new String[]{"old_path", "new_path"},
                        new String[]{"string", "string"},
                        new String[]{"Existing relative path", "Destination relative path"}
                )));

        tools.add(new ToolSpec(
                "delete_file",
                "Delete a file or directory (recursively) inside the workspace.",
                buildSchema(
                        new String[]{"path"},
                        new String[]{"string"},
                        new String[]{"Relative path to delete"}
                )));

        tools.add(new ToolSpec(
                "copy_file",
                "Copy a file or directory to a new location within the workspace.",
                buildSchema(
                        new String[]{"source_path", "destination_path"},
                        new String[]{"string", "string"},
                        new String[]{"Source relative path", "Destination relative path"}
                )));

        tools.add(new ToolSpec(
                "move_file",
                "Move a file or directory to a new location (copy + delete).",
                buildSchema(
                        new String[]{"source_path", "destination_path"},
                        new String[]{"string", "string"},
                        new String[]{"Source relative path", "Destination relative path"}
                )));

        tools.add(new ToolSpec(
                "search_files",
                "Search files using a Java regex pattern and return context-rich matches.",
                buildSchema(
                        new String[]{"directory", "regex_pattern"},
                        new String[]{"string", "string"},
                        new String[]{"Relative directory to scan", "Regex pattern to evaluate"}
                )));

        tools.add(new ToolSpec(
                "list_code_definition_names",
                "Enumerate class/function/component names inside a directory for quick discovery.",
                buildSchema(
                        new String[]{"directory"},
                        new String[]{"string"},
                        new String[]{"Relative directory containing source files"}
                )));

        tools.add(new ToolSpec(
                "ask_followup_question",
                "Pause execution and ask the user a clarifying question.",
                buildSchema(
                        new String[]{"question"},
                        new String[]{"string"},
                        new String[]{"Question to show the user"}
                )));

        tools.add(new ToolSpec(
                "attempt_completion",
                "Signal that the task is believed to be complete and provide a summary.",
                buildSchema(
                        new String[]{"summary"},
                        new String[]{"string"},
                        new String[]{"Concise summary of what was accomplished"}
                )));

        return tools;
    }

    /**
     * Enhanced schema builder with descriptions
     */
    private static JsonObject buildSchema(String[] keys, String[] types, String[] descriptions) {
        JsonObject schema = new JsonObject();
        schema.addProperty("type", "object");

        JsonObject props = new JsonObject();
        for (int i = 0; i < keys.length; i++) {
            JsonObject field = new JsonObject();
            field.addProperty("type", types[i]);
            if (descriptions != null && i < descriptions.length) {
                field.addProperty("description", descriptions[i]);
            }
            props.add(keys[i], field);
        }
        schema.add("properties", props);

        // required
        JsonArray req = new JsonArray();
        for (String k : keys) req.add(k);
        schema.add("required", req);
        return schema;
    }

    /**
     * Legacy schema builder for backward compatibility
     */
    private static JsonObject buildSchema(String[] keys, String[] types) {
        return buildSchema(keys, types, null);
    }
}