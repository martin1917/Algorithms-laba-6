package logic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utils.FileWrapper;

public class SolutionWithData {
    public static List<List<String>> externalSortTwoStageSimpleMerge(FileWrapper fileWithData) throws IOException {
        List<List<String>> allSteps = new ArrayList<List<String>>();
        
        FileWrapper fileA = new FileWrapper("other//files1//a.txt");
        FileWrapper fileB = new FileWrapper("other//files1//b.txt");
        FileWrapper fileC = new FileWrapper("other//files1//c.txt");
        
        fileWithData.open();
        fileB.openAndClear();
        fileC.openAndClear();

        fileA.openAndClear();
        fileA.copyFrom(fileWithData);
        fileWithData.close();
        
        allSteps.add(fileA.getAllNumbers());

        int len = 1;
        
        //выходим из цикла как только длина цепочки станет не меньше кол-во цифр
        while(len < fileA.getCount()) {
            fileB.clear();
            fileC.clear();

            //Переменная для определения файла для записи
            boolean diraction = true;

            //Разбиваем цепочки файла А по двум файлам B и C
            //цепочки под четными номерами   --> B
            //цепочки под нечетными номерами --> С
            for(int i = 0, len1 = Math.min(fileA.getCount() - i, len); i < fileA.getCount(); i += len1) {

                //цепочка может быть заполнена не до конца
                //чтобы не выйти за границы берем минимум
                //между кол-вом оставшихся чисел или len
                len1 = Math.min(fileA.getCount() - i, len);

                for(int j = i; j < i + len1; j++) {
                    int num = fileA.getNumberByPosition(j);
                    if(diraction) {
                        fileB.writeNumber(num);
                    } else {
                        fileC.writeNumber(num);
                    }
                }

                diraction = !diraction;
            }

            
            //объединяем файлы B и C в A
            mergeIntoFile(fileA, fileB, fileC, len);
            
            allSteps.add(fileB.getAllNumbers());
            allSteps.add(fileC.getAllNumbers());
            allSteps.add(List.of());
            allSteps.add(fileA.getAllNumbers());

            len *= 2;
        }

        //закрываем файлы
        fileA.close();
        fileB.close();
        fileC.close();

        return allSteps;
    }
    
    //объединяем 2 файла (B и C) в один (A)
    private static void mergeIntoFile(FileWrapper fileA, FileWrapper fileB, FileWrapper fileC, int len) throws IOException {
        fileA.clear();

        int countB = fileB.getCount();
        int countC = fileC.getCount();
        int minLen = Math.min(countB, countC);
        for(int i = 0; i < minLen; i += len) {
            int ib = i, ic = i;
            int borderB = Math.min(i + len, countB);
            int borderC = Math.min(i + len, countC);

            writeInOneFile(ib, borderB, ic, borderC, fileB, fileC, fileA);
        }

        if (countB + countC > fileA.getCount()) {
            fileA.copyFrom(fileB, minLen);
            fileA.copyFrom(fileC, minLen);
        }
    }

    //разбиваем файл на два (A --> B и C)
    private static void makeTwoFiles(FileWrapper readFile, FileWrapper writeFile1, FileWrapper writeFile2) throws IOException {
        boolean diraction = true;
        for(int i = 0; i < readFile.getCount(); i++) {
            int num = readFile.getNumberByPosition(i);

            if(diraction) {
                writeFile1.writeNumber(num);
            } else {
                writeFile2.writeNumber(num);
            }

            diraction = !diraction;
        }
    }

    // Пишем в файл упорядоченные цепочки из двух других файлов
    private static void writeInOneFile(FileWrapper readFile1, FileWrapper readFile2, FileWrapper writeFile) throws IOException {
        writeInOneFile(0, readFile1.getCount(), 0, readFile2.getCount(), readFile1, readFile2, writeFile);
    }

    private static void writeInOneFile(int i1, int j1, int i2, int j2, FileWrapper readFile1, FileWrapper readFile2, FileWrapper writeFile) throws IOException {
        boolean readedElem1 = false;
        boolean readedElem2 = false;
        int elem1 = -1;
        int elem2 = -1;
        while(i1 < j1 && i2 < j2) {
            if (!readedElem1) {
                elem1 = readFile1.getNumberByPosition(i1);
                readedElem1 = true;
            }
            if (!readedElem2) {
                elem2 = readFile2.getNumberByPosition(i2);
                readedElem2 = true;
            }

            if(elem1 < elem2) {
                writeFile.writeNumber(elem1);
                readedElem1 = false;
                i1++;
            } else {
                writeFile.writeNumber(elem2);
                readedElem2 = false;
                i2++;
            }
        }

        if(i1 < j1) {
            writeFile.writeNumber(elem1);
            i1++;
        }

        while(i1 < j1) {
            elem1 = readFile1.getNumberByPosition(i1);
            writeFile.writeNumber(elem1);
            i1++;
        }

        if(i2 < j2) {
            writeFile.writeNumber(elem2);
            i2++;
        }

        while(i2 < j2) {
            elem2 = readFile2.getNumberByPosition(i2);
            writeFile.writeNumber(elem2);
            i2++;
        }
    }

    private static void switchFile(int len, FileWrapper from1, FileWrapper from2, FileWrapper in1, FileWrapper in2, List<List<String>> allSteps) throws IOException {
        int count1 = from1.getCount();
        int count2 = from2.getCount();

        int minLen = Math.min(count1, count2);

        //Переменная для определения файла записи
        boolean diraction = true;

        for(int i = 0; i < minLen; i += len) {
            //начальные индексы текующих цепочек
            int i1 = i, i2 = i;

            //конечные индексы текующих цепочек
            int border1 = Math.min(i + len, count1);
            int border2 = Math.min(i + len, count2);

            //пишем в два других файла упорядоченные цепочки в зависимости от diraction
            //true  --> пишем из from1 и from2 в in1
            //false --> пишем из from1 и from2 в in2
            if(diraction){
                writeInOneFile(i1, border1, i2, border2, from1, from2, in1);
            } else {
                writeInOneFile(i1, border1, i2, border2, from1, from2, in2);
            }

            diraction = !diraction;
        }

        //нерасмотренные числа в одном из файлов копируем в in1 или in2
        //так как кол-во чисел в одном из них может быть больше minLen,
        //а итерировались мы до minLen
        if(from1.getCount() + from2.getCount() > in1.getCount() + in2.getCount()) {
            if(diraction) {
                in1.copyFrom(from1, minLen);
                in1.copyFrom(from2, minLen);
                
            } else {
                in2.copyFrom(from1, minLen);
                in2.copyFrom(from2, minLen);
            }
        }

        allSteps.add(in1.getAllNumbers());
        allSteps.add(in2.getAllNumbers());
        allSteps.add(List.of());

        //очищаем прочитанные файлы
        from1.clear();
        from2.clear();
    }

    public static List<List<String>> externalSortOneStageSimpleMerge(FileWrapper fileWithData) throws IOException {
        List<List<String>> allSteps = new ArrayList<List<String>>();

        FileWrapper fileA = new FileWrapper("other//files2//a.txt");
        FileWrapper fileB = new FileWrapper("other//files2//b.txt");
        FileWrapper fileC = new FileWrapper("other//files2//c.txt");
        FileWrapper fileD = new FileWrapper("other//files2//d.txt");
        FileWrapper fileE = new FileWrapper("other//files2//e.txt");

        fileWithData.open();
        fileB.openAndClear();
        fileC.openAndClear();
        fileD.openAndClear();
        fileE.openAndClear();

        fileA.openAndClear();
        fileA.copyFrom(fileWithData);
        fileWithData.close();
        
        allSteps.add(fileA.getAllNumbers());

        //разбиваем файл A на 2 файла B и C
        makeTwoFiles(fileA, fileB, fileC);
        allSteps.add(fileB.getAllNumbers());
        allSteps.add(fileC.getAllNumbers());
        allSteps.add(List.of());
        

        //true  --> читаем из B и C; пишем в D и E
        //false --> читаем из D и E; пишем в B и C
        boolean flag = true;

        int len = 1;

        //пока цепочек больше одной манипулируем файлами B, C, D, E
        while(2*len < fileA.getCount()) {
            

            if(flag) {
                switchFile(len, fileB, fileC, fileD, fileE, allSteps);
            } else {
                switchFile(len, fileD, fileE, fileB, fileC, allSteps);
            }

            flag = !flag;
            len *= 2;
        }

        //очищаем файл A
        //Объединяем файлы ((B и C) или (D и E)) в один (A)
        fileA.clear();
        if(flag) {
            writeInOneFile(fileB, fileC, fileA);
        } else {
            writeInOneFile(fileD, fileE, fileA);
        }
        allSteps.add(fileA.getAllNumbers());

        //закрываем все файлы
        fileA.close();
        fileB.close();
        fileC.close();
        fileD.close();

        return allSteps;
    }
}
