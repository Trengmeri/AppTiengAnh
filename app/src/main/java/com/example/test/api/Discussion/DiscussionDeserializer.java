package com.example.test.api.Discussion;

import com.example.test.model.Discussion;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DiscussionDeserializer implements JsonDeserializer<Discussion> {
    @Override
    public Discussion deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        int id = jsonObject.get("id").getAsInt();
        int userID = jsonObject.get("userId").getAsInt();
        int lessonId = jsonObject.get("lessonId").getAsInt();
        String content = jsonObject.get("content").getAsString();
        int numLike = jsonObject.get("numLike").getAsInt();

        // Xử lý replies đệ quy
        List<Discussion> replies = new ArrayList<>();
        if (jsonObject.has("replies") && jsonObject.get("replies").isJsonArray()) {
            JsonArray repliesArray = jsonObject.getAsJsonArray("replies");
            for (JsonElement replyElement : repliesArray) {
                Discussion reply = context.deserialize(replyElement, Discussion.class);
                replies.add(reply);
            }
        }

        // Trả về đối tượng Discussion đã parse
        Discussion discussion = new Discussion();
        discussion.setId(id);
        discussion.setUserID(userID);
        discussion.setLessonID(lessonId);
        discussion.setContent(content);
        discussion.setNumLike(numLike);
        discussion.setReplies(replies);
        return discussion;
    }
}
