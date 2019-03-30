import me.dylancurzon.pages.Page;
import me.dylancurzon.pages.PageTemplate;
import me.dylancurzon.pages.element.container.ImmutableContainer;
import me.dylancurzon.pages.util.Vector2i;

public class Test {

    public static void main(String[] args) {
        Page page = PageTemplate.builder()
            .setSize(Vector2i.of(256, 192))
            .doOnTick(() -> {
                System.out.println("Page - tick!");
            })
            .add(ImmutableContainer.builder()
                .add()
                .doOnCreate(element -> {
                    element.doOnTick(() -> {
                        System.out.println("Container - tick!");
                    });
                })
                .build())
            .build()
            .asMutable();

        long tickInterval = 1000 / 60;
        long lastTick = 0;
        while (true) {
            if (System.currentTimeMillis() - lastTick > tickInterval) {
                lastTick = System.currentTimeMillis();
                page.tick();
            }
        }
    }

}
