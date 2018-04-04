/**
 * Interfejs systemu zarzÄdzania konwesjami.
 *
 */
public interface ConversionManagementInterface {

    /**
     * Klasa ConversionResult jest wynikiem konwersji. Zawiera ona pola z wynikiem
     * konwersji i pola, w ktorych majÄ zostaÄ umieszczone referencje do danych,
     * ktore byĹy konwersji poddane.
     *
     */
    public class ConversionResult {
        public final ConverterInterface.DataPortionInterface leftChannelData;
        public final ConverterInterface.DataPortionInterface rightChannelData;
        public final long leftChannelConversionResult;
        public final long rightChannelConversionResult;

        public ConversionResult(ConverterInterface.DataPortionInterface leftChannelData,
                                ConverterInterface.DataPortionInterface rightChannelData, long leftChannelConversionResult,
                                long rightChannelConversionResult) {
            this.leftChannelData = leftChannelData;
            this.rightChannelData = rightChannelData;
            this.leftChannelConversionResult = leftChannelConversionResult;
            this.rightChannelConversionResult = rightChannelConversionResult;
        }

    }

    /**
     * Interfejs pozwalajÄcy na przekazanie wyniku konwersji.
     */
    public interface ConversionReceiverInterface {
        /**
         * Metoda pozwalajÄca na przekazanie wyniku konwersji. Metody nie wolno uĹźywaÄ
         * wspĂłĹbieĹźnie - nowy wynik moĹźe zostaÄ przekazany dopiero po zakoĹczeniu
         * metody result dla wyniku wczeĹniejszego. Wyniki muszÄ byÄ przekazywane wg.
         * rosnÄcego numeru identyfikujÄcego porcje danych.
         *
         * @param result
         *            wynik konwersji
         */
        public void result(ConversionResult result);
    }

    /**
     * Metoda ustala iloĹÄ rdzeni, ktĂłrych moĹźna uĹźywaÄ do konwersji danych. Liczba
     * ta jest ograniczeniem na maksymalnÄ iloĹÄ rĂłwnoczesnych wywoĹaĹ metody
     * convert. W przypadku zwiÄkszenia liczby dostÄpnych rdzeni moĹźliwe jest ich
     * natychmiastowe uĹźycie w celu zwiÄkszenia liczby jednoczeĹnie realizowanych
     * konwersji. W przypadku zmniejszenia liczby dostÄpnych rdzeni nie wymaga siÄ
     * przerywania konwersji, ktĂłre sÄ w toku - wystarczy aby w przez pewien okres
     * czasu program zarzÄdzajÄcy konwersjami nie uruchamiaĹ nowych konwersji - nowe
     * konwersje moĹźna uruchomiÄ dopiero gdy liczba realizowanych konwersji spadnie
     * poniĹźej ustawionego przez tÄ metodÄ limitu.
     *
     * @param cores
     *            ograniczenie liczby rdzeni, ktĂłre moga byÄ uĹźywane przez system do
     *            konwersji danych.
     */
    public void setCores(int cores);

    /**
     * Metoda pozwala na przekazanie obiektu odpowiedzialnego za wykonywanie
     * konwersji danych.
     *
     * @param converter
     *            konwerter danych.
     */
    public void setConverter(ConverterInterface converter);

    /**
     * Metoda umoĹźliwia przekazanie obiektu, do ktĂłrego naleĹźy przekazywaÄ wyniki
     * konwersji.
     *
     * @param receiver
     *            obiekt odbierajÄcy dane
     */
    public void setConversionReceiver(ConversionReceiverInterface receiver);

    /**
     * Za pomocÄ tej metody uĹźytkownik przekazuje do systemu porcjÄ danych do
     * konwersji. System ma pozwoliÄ na wspĂłĹbieĹźne przekazywanie danych. Metoda nie
     * moĹźe blokowaÄ pracy wÄtku przekazujÄcego porcjÄ danych na zbyt dĹugi okres
     * czasu, czyli jej zadaniem jest zapamiÄtanie danych przeznaczonych do
     * konwersji a nie jej wykonywanie.
     *
     * @param data
     *            dane przeznaczone do konwersji.
     */
    public void addDataPortion(ConverterInterface.DataPortionInterface data);
}