import me.dylancurzon.pages.Page;
import me.dylancurzon.pages.PageTemplate;
import me.dylancurzon.pages.element.container.ImmutableContainer;
import me.dylancurzon.pages.util.Vector2i;

public class Test {

    public static void main(String[] args) {
        Page page = PageTemplate.builder()
            .setSize(Vector2i.of(256, 192))
            .add(ImmutableContainer.builder()
                .add()
                .build())
            .build()
            .asMutable();
    }

}
