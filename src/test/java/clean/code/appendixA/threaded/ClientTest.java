package clean.code.appendixA.threaded;

import clean.code.appendixA.MessageUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

public class ClientTest {

    private static final int PORT = 18010;
    private static final int TIMEOUT = 2000;

    private Server server;
    private Thread serverThread;

    @Before
    public void createServer() throws Exception {
        try {
            server = new Server(PORT, TIMEOUT);
            serverThread = new Thread(server);
            serverThread.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @After
    public void shutdownServer() throws InterruptedException {
        if (server != null) {
            server.stopProcessing();
            serverThread.join();
        }
    }

    class TrivialClient implements Runnable {
        int clientNumber;

        TrivialClient(int clientNumber) {
            this.clientNumber = clientNumber;
        }

        @Override
        public void run() {
            try {
                connectSendReceive(clientNumber);
            } catch (IOException e) {
                e.printStackTrace();;
            }
        }
    }

    private void connectSendReceive(int i) throws IOException {
        System.out.printf("Client %2d: connection\n", i);
        Socket socket = new Socket("localhost", PORT);
        System.out.printf("Client %2d: sending message\n", i);
        MessageUtils.sendMessage(socket, Integer.toString(i));
        System.out.printf("Client %2d: getting reply\n", i);
        MessageUtils.getMessage(socket);
        System.out.printf("Client %2d: finished\n", i);
        socket.close();
    }

    @Test(timeout = 10000)
    public void shouldRunInUnder10Seconds() throws Exception {
        Thread[] threads = createThreads();
        startAllThreads(threads);
        waitForAllThreadsToFinish(threads);
    }

    private Thread[] createThreads() {
        return new Thread[10];
    }

    private void startAllThreads(Thread[] threads) {
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new TrivialClient(i));
            threads[i].start();
        }
    }

    private void waitForAllThreadsToFinish(Thread[] threads) throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }
    }
}