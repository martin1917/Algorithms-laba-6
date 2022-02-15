import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class FileWrapper {

    private RandomAccessFile file;
    private String fileName;
    private int countNumber;
    private boolean isOpen;


    public FileWrapper(String fileName) {
        this.fileName = fileName;
        this.isOpen = false;
        this.countNumber = 0;
    }

    public void open() throws FileNotFoundException{
        if(!isOpen){
            file = new RandomAccessFile(fileName, "rw");
            isOpen = true;
        }
    }

    public void close() throws IOException {
        if(isOpen) {
            file.close();
            isOpen = false;
        }
    }

    public void clear() throws FileNotFoundException {
        new PrintWriter(fileName).close();
        countNumber = 0;
    }

    public void fillByNumbers(int count) throws IOException {
        if(!isOpen) {
            throw new RuntimeException("File is closed");
        }

        clear();
        for(int i = 0; i < count; i++) {
            file.writeBytes(String.format("%d ", (int)(Math.random() * 100) + 1));
        }

        countNumber = count;
    }

    public void writeNumber(int num) throws IOException {
        if(!isOpen) {
            throw new RuntimeException("File is closed");
        }

        file.seek(file.length());
        file.writeBytes(String.format("%d", num) + " ");
        countNumber++;
    }

    public int getNumberByPosition(long pos) throws IOException {
        if(!isOpen) {
            throw new RuntimeException("File is closed");
        }

        file.seek(0);

        int countSpace = 0;
        int ch = -1;
        while(countSpace != pos && (ch = file.read()) != -1) {
            if (ch == 32) {
                countSpace++;
            }
        }
        
        StringBuilder sb = new StringBuilder();
        while((ch = file.read()) != 32 && ch != -1) {
            sb.append((char) ch);
        }

        return Integer.parseInt(sb.toString());
    }

    public List<Integer> getAllNumbers() throws IOException {
        file.seek(0);
        List<Integer> res = new ArrayList<Integer>(countNumber);

        String[] numbers = file.readLine().trim().split(" ");
        for(String num : numbers) {
            res.add(Integer.parseInt(num));
        }
        
        return res;
    }

    public void copyFrom(FileWrapper fromFile) throws IOException {
        copyFrom(fromFile, 0, fromFile.getCount());
    }

    public void copyFrom(FileWrapper fromFile, int start) throws IOException {
        copyFrom(fromFile, start, fromFile.getCount());
    }

    public void copyFrom(FileWrapper fromFile, int start, int end) throws IOException {
        for(int i = start; i < end; i++) {
            int num = fromFile.getNumberByPosition(i);
            writeNumber(num);
        }
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public int getCount() {
        return this.countNumber;
    }

    public void setCount(int count) {
        countNumber = count;
    }

    public String getFileName() {
        return this.fileName;
    }
} 
