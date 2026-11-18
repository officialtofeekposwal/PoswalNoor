# -*- coding: utf-8 -*-
"""
Deterministic Devanagari -> Casual Hinglish Transliteration Engine
for Poswal Noor (Farooq Khan Tanzil Translation).

Key Phonological Rules:
1. Decomposed NFD normalization handles all Nuktas:
   q (क़), kh (ख़), gh (ग़), z (ज़), f (फ़), d (ड़), dh (ढ़).
2. Pure ASCII Latin letters only. No diacritics or non-ASCII characters.
3. Schwa Deletion:
   - Word-final consonants ALWAYS delete inherent 'a' (naam, kitaab, rab, dil).
   - Words starting with independent vowel (uske, unke, apne, inke, isko):
     middle schwa is deleted.
   - Word-initial consonant (idx == 0) ALWAYS retains its vowel (baghair, zameen, madad, kaha, jana).
     Hindi words never drop the initial syllable's vowel unless written with halant (्).
   - Word-internal unstressed schwas are deleted (karta, dekhna, badla, barkat).
4. Casual Hinglish spelling conventions:
   - Final 'aa' in multi-syllable verbs/nouns -> 'a' (karta, badla, khuda, duniya).
   - Final 'ee' in multi-syllable words -> 'i' (apni, uski, nahi, kisi, hogi).
   - Medial 'ee' / 'oo' preserved (zameen, tareef, noor).
   - Full stop replaces Hindi Purna Viram (। -> .).
5. Normalized Islamic Terminology dictionary.
"""

import re
import unicodedata

ISLAMIC_DICT = {
    'अल्लाह': 'Allah',
    'अल्लाहु': 'Allahu',
    'अल्लाहू': 'Allahu',
    'रब': 'Rab',
    'रब्ब': 'Rab',
    'रहमान': 'Rehman',
    'रहीम': 'Raheem',
    'मालिक': 'Maalik',
    'ख़ालिक़': 'Khaliq',
    'खालिक': 'Khaliq',
    'ख़ालिक': 'Khaliq',
    'माबूद': 'Maabood',
    'परवरदिगार': 'Parwardigar',
    'ख़ुदा': 'Khuda',
    'खुदा': 'Khuda',
    'रसूल': 'Rasool',
    'रसूलों': 'Rasoolon',
    'रसूलन': 'Rasoolan',
    'नबी': 'Nabi',
    'नबियों': 'Nabiyon',
    'पैग़म्बर': 'Paigambar',
    'पैगंबर': 'Paigambar',
    'पैग़म्बरों': 'Paigambaron',
    'पैगंबरों': 'Paigambaron',
    'मुहम्मद': 'Muhammad',
    'मोहम्मद': 'Muhammad',
    'आदम': 'Aadam',
    'नूह': 'Nooh',
    'इब्राहीम': 'Ibrahim',
    'इस्माईल': 'Ismail',
    'इस्हाक़': 'Ishaq',
    'याक़ूब': 'Yaqoob',
    'यूसुफ़': 'Yusuf',
    'मूसा': 'Moosa',
    'हारून': 'Haroon',
    'दाऊद': 'Dawood',
    'सुलैमान': 'Sulaiman',
    'अय्यूब': 'Ayyub',
    'यूनुस': 'Yunus',
    'ज़करिया': 'Zakariya',
    'यह्या': 'Yahya',
    'ईसा': 'Isa',
    'मरियम': 'Maryam',
    'लूत': 'Loot',
    'हव्वा': 'Hawwa',
    'क़ुरआन': 'Quran',
    'क़ुरान': 'Quran',
    'कुरआन': 'Quran',
    'कुरान': 'Quran',
    'तौरात': 'Taurat',
    'इंजील': 'Injeel',
    'ज़बूर': 'Zaboor',
    'सहीफ़े': 'Sahife',
    'वह्य': 'Wahi',
    'वही': 'Wahi',
    'आयत': 'Ayat',
    'आयतें': 'Ayatein',
    'आयतों': 'Ayaton',
    'सूरह': 'Surah',
    'फ़रिश्ते': 'Farishte',
    'फ़रिश्तों': 'Farishton',
    'फ़रिश्ता': 'Farishta',
    'फरिश्ते': 'Farishte',
    'फरिश्तों': 'Farishton',
    'फरिश्ता': 'Farishta',
    'जिब्रील': 'Jibreel',
    'मीकाईल': 'Mikaeel',
    'इस्राफील': 'Israfeel',
    'इज़राईल': 'Izraeel',
    'शैतान': 'Shaitan',
    'इबलीस': 'Iblees',
    'जिन्न': 'Jinn',
    'जिन्नों': 'Jinnon',
    'अर्श': 'Arsh',
    'कुर्सी': 'Kursi',
    'रूह': 'Rooh',
    'क़यामत': 'Qayamat',
    'कयामत': 'Qayamat',
    'आख़िरत': 'Aakhirat',
    'आखिरत': 'Aakhirat',
    'जन्नत': 'Jannat',
    'जन्नतों': 'Jannaton',
    'जहन्नम': 'Jahannam',
    'दोज़ख़': 'Dozakh',
    'दोजख': 'Dozakh',
    'बरज़ख़': 'Barzakh',
    'अज़ाब': 'Azaab',
    'अजाब': 'Azaab',
    'सवाब': 'Sawab',
    'हश्र': 'Hashr',
    'हिसाब': 'Hisaab',
    'ईमान': 'Iman',
    'मोमिन': 'Momin',
    'मोमिनों': 'Mominon',
    'काफ़िर': 'Kafir',
    'काफ़िरों': 'Kafiron',
    'काफिर': 'Kafir',
    'काफिरों': 'Kafiron',
    'कुफ़्र': 'Kufr',
    'मुश्रिक': 'Mushrik',
    'मुश्रिकों': 'Mushrikon',
    'मुनाफ़िक़': 'Munafiq',
    'मुनाफ़िक़ों': 'Munafiqon',
    'मुनाफिक': 'Munafiq',
    'मुनाफिकों': 'Munafiqon',
    'शिर्क': 'Shirk',
    'नमाज़': 'Namaz',
    'नमाज़ें': 'Namazein',
    'नमाज': 'Namaz',
    'नमाजे': 'Namazein',
    'ज़कात': 'Zakat',
    'जकात': 'Zakat',
    'रोज़ा': 'Roza',
    'रोज़े': 'Roze',
    'रोजा': 'Roza',
    'रोजे': 'Roze',
    'हज': 'Hajj',
    'उमराह': 'Umrah',
    'तौबा': 'Tauba',
    'सजदा': 'Sajda',
    'रुकू': 'Ruku',
    'इबादत': 'Ibadat',
    'दुआ': 'Dua',
    'ज़िक्र': 'Zikr',
    'जिक्र': 'Zikr',
    'तक़वा': 'Taqwa',
    'तकवा': 'Taqwa',
    'सदक़ा': 'Sadqah',
    'सदका': 'Sadqah',
    'जिहाद': 'Jihad',
    'हलाल': 'Halal',
    'हराम': 'Haram',
    'गुनाह': 'Gunah',
    'गुनाहों': 'Gunahon',
    'मग़फ़िरत': 'Maghfirat',
    'मगफिरत': 'Maghfirat',
    'रहमत': 'Rehmat',
    'बरकत': 'Barkat',
    'हिदायत': 'Hidayat',
    'हक़': 'Haq',
    'हक': 'Haq',
    'बातिल': 'Baatil',
    'क़िबला': 'Qibla',
    'किबला': 'Qibla',
    'काबा': 'Kaaba',
    'मस्जिद': 'Masjid',
    'मस्जिदों': 'Masjidon',
    'सुन्नत': 'Sunnat',
    'हदीस': 'Hadith',
    'फ़ज़्ल': 'Fazl',
    'फज्ल': 'Fazl',
    'सब्र': 'Sabr',
    'शुक्र': 'Shukr',
    'शहादत': 'Shahadat',
    'शहीद': 'Shaheed',
    'शहीदों': 'Shaheedon',
    'अहद': 'Ahad',
    'ख़ैर': 'Khair',
    'खैर': 'Khair',
    'फ़ितना': 'Fitna',
    'फितना': 'Fitna',
    'फ़िरऔन': 'Firaun',
    'फिरऔन': 'Firaun',
    'क़ारून': 'Qaroon',
    'कारून': 'Qaroon',
    'हामान': 'Haman',
    'याजूज': 'Yajuj',
    'माजूज': 'Majuj',
}

ISLAMIC_DICT_NFD = {unicodedata.normalize('NFD', k): v for k, v in ISLAMIC_DICT.items()}

COMMON_WORDS = {
    'है': 'hai', 'हैं': 'hain', 'हो': 'ho', 'हुए': 'hue', 'हुआ': 'hua', 'हुई': 'hui',
    'होगा': 'hoga', 'होगी': 'hogi', 'होंगे': 'honge', 'था': 'tha', 'थी': 'thi', 'थे': 'the',
    'का': 'ka', 'की': 'ki', 'के': 'ke', 'को': 'ko', 'से': 'se', 'में': 'mein', 'पर': 'par',
    'ने': 'ne', 'तक': 'tak', 'लिए': 'liye', 'लिये': 'liye', 'वास्ते': 'waaste',
    'और': 'aur', 'व': 'wa', 'या': 'ya', 'तो': 'to', 'भी': 'bhi', 'ही': 'hi',
    'नहीं': 'nahi', 'न': 'na', 'ना': 'na', 'मत': 'mat',
    'जो': 'jo', 'वो': 'wo', 'वह': 'woh', 'यह': 'yeh', 'ये': 'ye', 'वे': 've',
    'इस': 'is', 'उस': 'us', 'इन': 'in', 'उन': 'un', 'जिस': 'jis', 'तिस': 'tis',
    'किसे': 'kise', 'जिसे': 'jise', 'उसे': 'use', 'इसे': 'ise', 'किस': 'kis',
    'कुछ': 'kuch', 'कोई': 'koi', 'सब': 'sab', 'तमाम': 'tamaam',
    'सारे': 'saare', 'सारा': 'saara', 'सारी': 'saari',
    'बहुत': 'bahut', 'कम': 'kam', 'ज़्यादा': 'zyada', 'ज्यादा': 'zyada',
    'अगर': 'agar', 'मगर': 'magar', 'लेकिन': 'lekin', 'बल्कि': 'balki',
    'क्योंकि': 'kyunki', 'क्यूंकि': 'kyunki', 'ताकि': 'taaki', 'ताँकि': 'taaki',
    'जब': 'jab', 'तब': 'tab', 'कब': 'kab', 'अब': 'ab',
    'यहाँ': 'yahan', 'यहां': 'yahan', 'वहाँ': 'wahan', 'वहां': 'wahan',
    'जहाँ': 'jahan', 'जहां': 'jahan', 'कहाँ': 'kahan', 'कहां': 'kahan',
    'पहले': 'pehle', 'बाद': 'baad', 'पीछे': 'peeche', 'आगे': 'aage',
    'ऊपर': 'oopar', 'नीचे': 'neeche', 'अंदर': 'andar', 'बाहर': 'baahar',
    'साथ': 'saath', 'पास': 'paas', 'दूर': 'door', 'सामने': 'saamne',
    'दरमियान': 'darmiyan', 'बीच': 'beech',
    'तरफ़': 'taraf', 'तरफ': 'taraf', 'ओर': 'or', 'तरह': 'tarah',
    'जैसे': 'jaise', 'वैसा': 'waisa', 'ऐसी': 'aisi', 'ऐसा': 'aisa', 'ऐसे': 'aise',
    'कौन': 'kaun', 'क्या': 'kya', 'क्यों': 'kyon',
    'कहो': 'kaho', 'कहते': 'kahte', 'कहा': 'kaha', 'बोलो': 'bolo',
    'सुना': 'suna', 'सुनो': 'suno', 'देखो': 'dekho', 'देखा': 'dekha',
    'जानते': 'jaante', 'जाना': 'jaana', 'समझे': 'samjhe', 'समझो': 'samjho',
    'मानो': 'maano', 'माना': 'maana', 'डरो': 'daro', 'डरते': 'darte',
    'करो': 'karo', 'किया': 'kiya', 'कीजिए': 'kijiye', 'करते': 'karte',
    'करता': 'karta', 'करती': 'karti', 'करना': 'karna',
    'दिलाया': 'dilaya', 'दिया': 'diya', 'दी': 'di', 'देंगे': 'denge',
    'पाया': 'paaya', 'रखा': 'rakha', 'रखे': 'rakhe',
    'बनाया': 'banaya', 'बनाए': 'banaye', 'भेजा': 'bheja', 'भेजे': 'bheje',
    'उतारा': 'utaara', 'उतारी': 'utaari',
    'तारीफ़': 'tareef', 'तारीफ': 'tareef', 'इनाम': 'inaam',
    'रास्ता': 'raasta', 'सीधा': 'seedha',
    'बड़ा': 'bada', 'बड़े': 'bade', 'बड़ी': 'badi', 'छोटा': 'chhota',
    'नेक': 'nek', 'बुरा': 'bura', 'बुरे': 'bure', 'बुरी': 'buri',
    'हक़ीक़त': 'haqeeqat', 'हकीकत': 'haqeeqat',
    'किताब': 'kitaab', 'लोग': 'log', 'लोगों': 'logon',
    'इंसान': 'insaan', 'इंसानों': 'insaanon',
    'बंदे': 'bande', 'बन्दों': 'bandon', 'बन्दे': 'bande', 'बंदा': 'banda', 'बन्दा': 'banda',
    'औरत': 'aurat', 'औरतें': 'auratein', 'मर्द': 'mard',
    'दिल': 'dil', 'दिलों': 'dilon',
    'आँख': 'aankh', 'आँखें': 'aankhein', 'कान': 'kaan', 'ज़ुबान': 'zubaan',
    'हाथ': 'haath', 'पाँव': 'paanw', 'पॉव': 'paanw', 'मियॉ': 'miyan',
    'जहान': 'jahaan', 'आसमान': 'aasmaan', 'आसमानों': 'aasmaanon', 'आसमानॊं': 'aasmaanon',
    'ज़मीन': 'zameen', 'ज़मीन': 'zameen', 'दिन': 'din', 'रात': 'raat',
    'सुबह': 'subah', 'शाम': 'shaam', 'वक्त': 'waqt', 'वक़्त': 'waqt',
    'ज़माना': 'zamaana', 'ज़िंदगी': 'zindagi', 'मौत': 'maut', 'क़ब्र': 'qabr', 'क़ब्रों': 'qabron',
    'ऐ': 'Ae', 'सिर्फ': 'sirf', 'सिर्फ़': 'sirf', 'साफ': 'saaf', 'साफ़': 'saaf',
    'माफ': 'maaf', 'माफ़': 'maaf', 'बरहक़': 'barhaq', 'बरहक': 'barhaq',
    'बेनियाज़': 'beniyaaz', 'बेनियाज': 'beniyaaz',
    'अहाता': 'ahaata', 'निगेहदाश्त': 'nigehdaasht', 'मुश्किल': 'mushkil',
    'आलीशान': 'aaleeshaan', 'बुज़ुर्ग': 'buzurg', 'बुजुर्ग': 'buzurg', 'बुजुर्ग़': 'buzurg',
    'हरगिज़': 'hargiz', 'हरगिज': 'hargiz', 'अनक़रीब': 'anqareeb', 'अनकरीब': 'anqareeb',
    'पालने': 'paalne', 'वाले': 'waale', 'वाला': 'waala', 'वाली': 'waali',
    'बेशक': 'beshak', 'देखभाल': 'dekhbhaal', 'कज़ी': 'kazi', 'क़ज़ी': 'kazi',
    'बारगाह': 'baargaah', 'इनायत': 'inaayat', 'कामयाबी': 'kaamyaabi',
    'हिकमत': 'hikmat', 'क़सम': 'qasam', 'कसम': 'qasam', 'हमसर': 'hamsar',
    'बग़ैर': 'baghair', 'बगैर': 'baghair',
    'उसने': 'usne', 'उसके': 'uske', 'उसको': 'usko', 'उसका': 'uska', 'उसकी': 'uski', 'उससे': 'usse', 'उसमें': 'usmein', 'उसपर': 'uspar',
    'इसने': 'isne', 'इसके': 'iske', 'इसको': 'isko', 'इसका': 'iska', 'इसकी': 'iski', 'इससे': 'isse', 'इसमें': 'ismein', 'इसपर': 'ispar',
    'उन्होंने': 'unhone', 'उनके': 'unke', 'उनको': 'unko', 'उनका': 'unka', 'उनकी': 'unki', 'उनसे': 'unse', 'उनमें': 'unmein', 'उनपर': 'unpar',
    'इन्होंने': 'inhone', 'इनके': 'inke', 'इनको': 'inko', 'इनका': 'inka', 'इनकी': 'inki', 'इनसे': 'inse', 'इनमें': 'inmein', 'इनपर': 'inpar',
    'जिसने': 'jisne', 'जिसके': 'jiske', 'जिसको': 'jisko', 'जिसका': 'jiska', 'जिसकी': 'jiski', 'जिससे': 'jisse', 'जिसमें': 'jismein', 'जिसपर': 'jispar',
    'किसने': 'kisne', 'किसके': 'kiske', 'किसको': 'kisko', 'किसका': 'kiska', 'किसकी': 'kiski', 'किससे': 'kisse',
    'अपने': 'apne', 'अपना': 'apna', 'अपनी': 'apni', 'आपस': 'aapas',
    'तुमने': 'tumne', 'तुम्हारे': 'tumhaare', 'तुम्हारा': 'tumhaara', 'तुम्हारी': 'tumhaari', 'तुमको': 'tumko', 'तुमसे': 'tumse',
    'हमने': 'humne', 'हमारे': 'hamaare', 'हमारा': 'hamaara', 'हमारी': 'hamaari', 'हमको': 'humko', 'हमसे': 'humse',
    'इजाज़त': 'ijaazat', 'इजाज़त': 'ijaazat', 'सिफ़ारिश': 'sifaarish', 'सिफ़ारिश': 'sifaarish', 'मौजूद': 'maujood',
}
COMMON_WORDS_NFD = {unicodedata.normalize('NFD', k): v for k, v in COMMON_WORDS.items()}

VOWELS = {
    '\u0905': 'a', '\u0906': 'aa', '\u0907': 'i', '\u0908': 'ee', '\u0909': 'u', '\u090a': 'oo',
    '\u090b': 'ri', '\u090c': 'i', '\u090e': 'e', '\u090f': 'e', '\u0910': 'ai', '\u0913': 'o',
    '\u0914': 'au', '\u0911': 'o', '\u090d': 'e'
}

MATRAS = {
    '\u093e': 'aa', '\u093f': 'i', '\u0940': 'ee', '\u0941': 'u', '\u0942': 'oo',
    '\u0943': 'ri', '\u0944': 'ri', '\u0945': 'e', '\u0946': 'e', '\u0947': 'e',
    '\u0948': 'ai', '\u0949': 'o', '\u094a': 'o', '\u094b': 'o', '\u094c': 'au',
    '\u0962': 'l', '\u0963': 'ee'
}

CONSONANTS = {
    '\u0915': 'k', '\u0916': 'kh', '\u0917': 'g', '\u0918': 'gh', '\u0919': 'ng',
    '\u091a': 'ch', '\u091b': 'chh', '\u091c': 'j', '\u091d': 'jh', '\u091e': 'ny',
    '\u091f': 't', '\u0920': 'th', '\u0921': 'd', '\u0922': 'dh', '\u0923': 'n',
    '\u0924': 't', '\u0925': 'th', '\u0926': 'd', '\u0927': 'dh', '\u0928': 'n',
    '\u092a': 'p', '\u092b': 'ph', '\u092c': 'b', '\u092d': 'bh', '\u092e': 'm',
    '\u092f': 'y', '\u0930': 'r', '\u0932': 'l', '\u0935': 'v',
    '\u0936': 'sh', '\u0937': 'sh', '\u0938': 's', '\u0939': 'h'
}

NUKTA_CONSONANTS = {
    '\u0915': 'q', '\u0916': 'kh', '\u0917': 'gh', '\u091c': 'z',
    '\u0921': 'd', '\u0922': 'dh', '\u092b': 'f', '\u092f': 'y'
}

def transliterate_word(clean_word_nfd):
    if not clean_word_nfd:
        return ""

    if clean_word_nfd in ISLAMIC_DICT_NFD:
        return ISLAMIC_DICT_NFD[clean_word_nfd]

    if clean_word_nfd in COMMON_WORDS_NFD:
        return COMMON_WORDS_NFD[clean_word_nfd]

    chars = []
    i = 0
    w_len = len(clean_word_nfd)
    while i < w_len:
        ch = clean_word_nfd[i]
        if i + 1 < w_len and clean_word_nfd[i+1] == '\u093c':
            nukta_val = NUKTA_CONSONANTS.get(ch, CONSONANTS.get(ch, ''))
            chars.append(('C', nukta_val, ch, True))
            i += 2
            continue
        elif ch in CONSONANTS:
            chars.append(('C', CONSONANTS[ch], ch, False))
        elif ch in VOWELS:
            chars.append(('V', VOWELS[ch], ch, False))
        elif ch in MATRAS:
            chars.append(('M', MATRAS[ch], ch, False))
        elif ch == '\u094d': # Halant
            chars.append(('H', '', ch, False))
        elif ch in ['\u0902', '\u0901']: # Anusvara / Chandrabindu
            chars.append(('N', 'n', ch, False))
        elif ch == '\u0903': # Visarga
            chars.append(('M', 'h', ch, False))
        else:
            chars.append(('O', ch, ch, False))
        i += 1

    num_tok = len(chars)
    total_consonants = sum(1 for c in chars if c[0] == 'C')
    starts_with_vowel = (num_tok > 0 and chars[0][0] == 'V')

    output = []
    for idx in range(num_tok):
        ttype, val, orig, is_nukta = chars[idx]

        if ttype == 'V':
            output.append(val)
        elif ttype == 'M':
            if orig == '\u0940' and idx == num_tok - 1 and total_consonants > 1:
                output.append('i')
            elif orig == '\u093e' and idx == num_tok - 1 and total_consonants > 1:
                output.append('a')
            else:
                output.append(val)
        elif ttype == 'N':
            if idx + 1 < num_tok and chars[idx+1][0] == 'C' and chars[idx+1][2] in ['\u092a', '\u092b', '\u092c', '\u092d', '\u092e']:
                output.append('m')
            else:
                output.append('n')
        elif ttype == 'H':
            pass
        elif ttype == 'C':
            output.append(val)
            has_next = (idx + 1 < num_tok)
            if not has_next:
                # Final consonant loses schwa
                pass
            else:
                next_tok = chars[idx + 1]
                if next_tok[0] in ['M', 'H']:
                    pass
                elif next_tok[0] == 'N':
                    output.append('a')
                elif next_tok[0] == 'C':
                    # Consonant followed by consonant:
                    # Initial consonant of the word (idx == 0) ALWAYS keeps inherent vowel 'a'
                    # unless written with halant (्). E.g. baghair, zameen, madad, kaha, jana.
                    if idx == 0:
                        output.append('a')
                    # Word started with independent vowel (uske, unke, apne, inke):
                    elif starts_with_vowel and idx == 1 and idx + 2 < num_tok and chars[idx + 2][0] == 'M':
                        pass
                    # Inside 3+ consonant word followed by C + Matra:
                    elif idx + 2 < num_tok and chars[idx + 2][0] == 'M':
                        pass
                    elif idx + 2 == num_tok:
                        output.append('a')
                    else:
                        output.append('a')
                elif next_tok[0] == 'V':
                    pass
                else:
                    output.append('a')
        else:
            output.append(val)

    res = "".join(output)
    res = re.sub(r'([aeiou])aa', r'\1', res)
    res = re.sub(r'iy([ae])', r'iy\1', res)
    return res

def transliterate_text(text):
    if not text:
        return ""

    # Pre-process Hindi Purna Viram and strip orphaned nuktas / typist artifacts
    text = text.replace('\u0964', '. ').replace('\u0965', '. ')
    # Fix typist typos in Tanzil where \u090c (ऌ) was typed instead of इ
    text = text.replace('\u090c', '\u0907')
    # Strip isolated nuktas preceded by space
    text = re.sub(r'(?<=\s)\u093c', '', text)
    # Convert to canonical NFD
    text_nfd = unicodedata.normalize('NFD', text)

    tokens = re.split(r'([^\u0900-\u097Fa-zA-Z0-9]+)', text_nfd)
    result = []

    for tok in tokens:
        if not tok:
            continue
        if re.search(r'[\u0900-\u097F]', tok):
            t_clean = tok.strip()
            if t_clean in ISLAMIC_DICT_NFD:
                trans = ISLAMIC_DICT_NFD[t_clean]
            elif t_clean in COMMON_WORDS_NFD:
                trans = COMMON_WORDS_NFD[t_clean]
            else:
                trans = transliterate_word(tok)
            result.append(trans)
        else:
            result.append(tok)

    res_str = "".join(result)

    # Strip any combining diacritics (U+0300..U+036F) or leftover nuktas
    res_str = re.sub(r'[\u0300-\u036F\u093c]', '', res_str)

    sentences = re.split(r'(\. |\? |\! |; )', res_str)
    cap_sentences = []
    for s in sentences:
        if s and s[0].islower():
            s = s[0].upper() + s[1:]
        cap_sentences.append(s)
    res_str = "".join(cap_sentences)

    for hin, eng in ISLAMIC_DICT.items():
        pattern = r'\b' + re.escape(eng.lower()) + r'\b'
        res_str = re.sub(pattern, eng, res_str, flags=re.IGNORECASE)

    res_str = re.sub(r'[ \t]+', ' ', res_str).strip()
    res_str = re.sub(r'\s+([.,?!])', r'\1', res_str)
    return res_str

if __name__ == '__main__':
    import sqlite3
    conn = sqlite3.connect('app/src/main/assets/database/poswal_noor.db')
    cursor = conn.cursor()
    test_ayahs = [(1,1), (1,2), (1,5), (1,7), (2,1), (2,2), (2,255), (3,144), (112,1), (112,2), (112,3), (112,4), (114,1)]
    for s, a in test_ayahs:
        cursor.execute("SELECT translation_hi FROM content_items WHERE type = 'quran' AND surah = ? AND ayah = ?", (s, a))
        row = cursor.fetchone()
        if row:
            hi = row[0]
            hing = transliterate_text(hi)
            print(f"[{s}:{a}]")
            print(f"  HI:   {hi}")
            print(f"  HING: {hing}\n")
