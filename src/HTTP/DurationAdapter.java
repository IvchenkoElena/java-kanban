package HTTP;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

class DurationAdapter extends TypeAdapter<Duration> { //вариант в минутах

    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(Long.valueOf(duration.toMinutes()).toString());
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        if (jsonReader == null) {
            return null;
        }
        return Duration.ofMinutes(Long.parseLong(jsonReader.nextString()));
    }
}