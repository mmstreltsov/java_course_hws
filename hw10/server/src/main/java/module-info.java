module server {
    opens ru.hse.producer to client;
    exports ru.hse.producer;
}