public interface ConverterInterface {
    /**
     * Nazwa kanaĹu.
     */
    public enum Channel {
        LEFT_CHANNEL, RIGHT_CHANNEL;
    }

    /**
     * Dane do przetworzenia
     */
    public interface DataPortionInterface {
        /**
         * Numer identyfikacyjny porcji danych. Numery dla danego kanaĹu sÄ zawsze
         * unikalne. Numer identyfikacyjny pierwszej porcji danych to 1. Ta sama wartoĹÄ
         * numeru identyfikacyjnego zgĹaszana jest dwa razy: jeden raz dla LEFT_CHANNEL
         * i jeden raz dla RIGHT_CHANNEL.
         *
         * @return numer identyfikacyjny
         */
        public int id();

        /**
         * Dane przekazywane w porcji danych.
         *
         * @return dane do przetworzenia
         */
        public int[] data();

        /**
         * Identyfikacja kanaĹu powiÄzanego z danymi.
         *
         * @return kanaĹ
         */
        public Channel channel();
    }

    /**
     * Metoda realizujÄca konwersjÄ danych. Dane zawarte w obiekcie zgodnym z
     * DataPortionInterface przetwarzane sÄ do jednej liczby typu long. Metoda moĹźe
     * byÄ wywoĹywana wspĂłĹbieĹźnie.
     *
     * @param data
     *            dane wejĹciowe
     * @return wynik przetwarzania danych.
     */
    public long convert(DataPortionInterface data);
}