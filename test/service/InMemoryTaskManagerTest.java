import org.junit.jupiter.api.BeforeEach;
import service.InMemoryTaskManager;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    //InMemoryTaskManager inMemoryTaskManager; // эта строка не нужна ни для чего?

    @BeforeEach
    void setUp() {
        super.taskManager = new InMemoryTaskManager();
    }

    //не смогла придумать, какие методы вынести сюда
}