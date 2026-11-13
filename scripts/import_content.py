"""
Poswal Noor Content Import & Room DB Builder
Validates and populates authentic Quran & Hadith content into SQLite DB for Room.
Includes English, Hinglish (Roman Urdu/Hindi), Hindi, and Urdu.
"""

import os
import sqlite3
import re

DB_DIR = os.path.join("app", "src", "main", "assets", "database")
DB_PATH = os.path.join(DB_DIR, "poswal_noor.db")

def count_words(text: str) -> int:
    words = re.findall(r"[\w']+", text)
    return len(words)

ITEMS = [
    # --- NAMAZ / PRAYER ---
    {
        "type": "quran",
        "arabic": "وَأَقِيمُوا الصَّلَاةَ وَآتُوا الزَّكَاةَ وَارْكَعُوا مَعَ الرَّاكِعِينَ",
        "translation_en": "And establish prayer and give zakah and bow with those who bow [in worship and obedience].",
        "translation_hinglish": "Aur namaz qayam karo aur zakat do, aur ruku karne walon ke sath ruku karo.",
        "translation_hi": "और नमाज़ क़ायम करो और ज़कात दो, और झुकने वालों के साथ झुको।",
        "translation_ur": "اور نماز قائم کرو اور زکوٰۃ دو اور رکوع کرنے والوں کے ساتھ رکوع کرو۔",
        "reference": "Surah Al-Baqarah 2:43",
        "book": "quran",
        "number": 50,
        "grade": "Mutawatir",
        "topic": "namaz"
    },
    {
        "type": "quran",
        "arabic": "إِنَّ الصَّلَاةَ تَنْهَىٰ عَنِ الْفَحْشَاءِ وَالْمُنكَرِ",
        "translation_en": "Indeed, prayer prohibits immorality and wrongdoing.",
        "translation_hinglish": "Beshak namaz behayai aur burai se rokti hai.",
        "translation_hi": "निस्संदेह नमाज़ बुराई और निर्लज्जता से रोकती है।",
        "translation_ur": "بے شک نماز بے حیائی اور برائی سے روکتی ہے۔",
        "reference": "Surah Al-Ankabut 29:45",
        "book": "quran",
        "number": 3385,
        "grade": "Mutawatir",
        "topic": "namaz"
    },
    {
        "type": "hadith",
        "arabic": "بُنِيَ الإِسْلاَمُ عَلَى خَمْسٍ: شَهَادَةِ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَإِقَامِ الصَّلاَةِ، وَإِيتَاءِ الزَّكَاةِ، وَالحَجِّ، وَصَوْمِ رَمَضَانَ",
        "translation_en": "Islam is based on five principles: To testify that none has the right to be worshipped but Allah and Muhammad is Allah's Apostle, to offer the prayers dutifully and perfectly, to pay Zakat, to perform Hajj, and to observe fast during Ramadan.",
        "translation_hinglish": "Islam ki buniyad 5 cheezon par hai: Is baat ki gawahi dena ke Allah ke siwa koi mabood nahi aur Muhammad Allah ke Rasool hain, namaz qayam karna, zakat dena, hajj karna aur Ramadan ke roze rakhna.",
        "translation_hi": "इस्लाम की बुनियाद पाँच बातों पर है: इस बात की गवाही देना कि अल्लाह के सिवा कोई पूज्य नहीं और मुहम्मद अल्लाह के रसूल हैं, नमाज़ क़ायम करना, ज़कात देना, हज करना और रमज़ान के रोज़े रखना।",
        "translation_ur": "اسلام کی بنیاد پانچ چیزوں پر ہے: اس بات کی گواہی دینا کہ اللہ کے سوا کوئی معبود نہیں اور محمد اللہ کے رسول ہیں، نماز قائم کرنا، زکوٰۃ دینا، حج کرنا اور رمضان کے روزے رکھنا۔",
        "reference": "Sahih al-Bukhari 8",
        "book": "bukhari",
        "number": 8,
        "grade": "Sahih",
        "topic": "namaz"
    },
    {
        "type": "hadith",
        "arabic": "الصَّلَوَاتُ الْخَمْسُ وَالْجُمُعَةُ إِلَى الْجُمُعَةِ كَفَّارَاتٌ لِمَا بَيْنَهُنَّ مَا لَمْ تُغْشَ الْكَبَائِرُ",
        "translation_en": "The five daily prayers and Friday prayer to the next Friday prayer are expiations for whatever sins occur between them, so long as major sins are avoided.",
        "translation_hinglish": "Paanchon namazein aur ek Juma doosre Juma tak gunahon ka kaffara hain jab tak kabira gunahon se bacha jaye.",
        "translation_hi": None,
        "translation_ur": "پانچوں نمازیں اور جمعہ اگلے جمعہ تک گناہوں کا کفارہ ہیں جب تک کہ کبیرہ گناہوں سے بچا جائے۔",
        "reference": "[Muslim 233]",
        "book": "muslim",
        "number": 233,
        "grade": "Sahih",
        "topic": "namaz"
    },

    # --- SABR / PATIENCE ---
    {
        "type": "quran",
        "arabic": "يَا أَيُّهَا الَّذِينَ آمَنُوا اسْتَعِينُوا بِالصَّبْرِ وَالصَّلَاةِ ۚ إِنَّ اللَّهَ مَعَ الصَّابِرِينَ",
        "translation_en": "O you who have believed, seek help through patience and prayer. Indeed, Allah is with the patient.",
        "translation_hinglish": "Aye imaan walo! Sabr aur namaz ke zariye madad chaho, yaqeenan Allah sabr karne walon ke sath hai.",
        "translation_hi": "हे ईमान लाने वालो! सब्र (धैर्य) और नमाज़ से मदद चाहो। निश्चय ही अल्लाह सब्र करने वालों के साथ है।",
        "translation_ur": "اے ایمان والو! صبر اور نماز کے ذریعے مدد چاہو، یقیناً اللہ صبر کرنے والوں کے ساتھ ہے۔",
        "reference": "Surah Al-Baqarah 2:153",
        "book": "quran",
        "number": 160,
        "grade": "Mutawatir",
        "topic": "sabr"
    },
    {
        "type": "quran",
        "arabic": "إِنَّمَا يُوَفَّى الصَّابِرُونَ أَجْرَهُم بِغَيْرِ حِسَابٍ",
        "translation_en": "Indeed, the patient will be given their reward without account.",
        "translation_hinglish": "Sabr karne walon hi ko unka ajar be-hisaab diya jayega.",
        "translation_hi": "धैर्य रखने वालों को तो उनका प्रतिदान बिना हिसाब के दिया जाएगा।",
        "translation_ur": "صبر کرنے والوں ہی کو ان کا اجر بے حساب دیا جائے گا۔",
        "reference": "Surah Az-Zumar 39:10",
        "book": "quran",
        "number": 4068,
        "grade": "Mutawatir",
        "topic": "sabr"
    },
    {
        "type": "hadith",
        "arabic": "وَمَنْ يَتَصَبَّرْ يُصَبِّرْهُ اللَّهُ، وَمَا أُعْطِيَ أَحَدٌ عَطَاءً خَيْرًا وَأَوْسَعَ مِنَ الصَّبْرِ",
        "translation_en": "Whoever remains patient, Allah will bestow patience upon him, and he who is given patience has been given the best and most comprehensive gift.",
        "translation_hinglish": "Jo sabr karne ki koshish karta hai, Allah use sabr deta hai, aur kisi ko sabr se behtar aur kushaada tohfa nahi mila.",
        "translation_hi": "जो धैर्य का प्रयास करता है, अल्लाह उसे धैर्य प्रदान करता है, और किसी को भी धैर्य से बेहतर और विशाल उपहार नहीं दिया गया।",
        "translation_ur": "جو صبر کرنے کی کوشش کرتا ہے اللہ اسے صبر عطا فرماتا ہے، اور کسی کو صبر سے بہتر اور کشادہ تحفہ نہیں دیا گیا۔",
        "reference": "Sahih al-Bukhari 1469",
        "book": "bukhari",
        "number": 1469,
        "grade": "Sahih",
        "topic": "sabr"
    },
    {
        "type": "hadith",
        "arabic": "عَجَبًا لأَمْرِ الْمُؤْمِنِ إِنَّ أَمْرَهُ كُلَّهُ خَيْرٌ إِنْ أَصَابَتْهُ سَرَّاءُ شَكَرَ فَكَانَ خَيْرًا لَهُ وَإِنْ أَصَابَتْهُ ضَرَّاءُ صَبَرَ فَكَانَ خَيْرًا لَهُ",
        "translation_en": "How wonderful is the affair of the believer, for his affairs are all good. If something good happens to him, he is thankful and that is good for him; and if something harmful happens to him, he is patient and that is good for him.",
        "translation_hinglish": "Momin ka maamla ajeeb hai, uske har kaam me khair hai: agar khushi mile to shukr karta hai aur agar takleef pahuche to sabr karta hai.",
        "translation_hi": None,
        "translation_ur": "مومن کا معاملہ بھی خوب ہے، اس کے تمام معاملات میں خیر ہی خیر ہے۔ اگر اسے خوشحالی ملے تو شکر کرتا ہے تو یہ اس کے لیے بہتر ہے، اور اگر تکلیف پہنچے تو صبر کرتا ہے تو یہ بھی اس کے لیے بہتر ہے۔",
        "reference": "[Muslim 2999]",
        "book": "muslim",
        "number": 2999,
        "grade": "Sahih",
        "topic": "sabr"
    },

    # --- AKHLAQ / CHARACTER & MANNERS ---
    {
        "type": "quran",
        "arabic": "وَإِنَّكَ لَعَلَىٰ خُلُقٍ عَظِيمٍ",
        "translation_en": "And indeed, you are of a great moral character.",
        "translation_hinglish": "Aur beshak aap (saw) akhlaaq ke aala tareen darje par hain.",
        "translation_hi": "और निश्चय ही तुम एक महान चरित्र पर आसीन हो।",
        "translation_ur": "اور یقیناً آپ اخلاق کے اعلیٰ ترین درجے پر ہیں۔",
        "reference": "Surah Al-Qalam 68:4",
        "book": "quran",
        "number": 5275,
        "grade": "Mutawatir",
        "topic": "akhlaq"
    },
    {
        "type": "hadith",
        "arabic": "إِنَّ مِنْ خِيَارِكُمْ أَحْسَنَكُمْ أَخْلاَقًا",
        "translation_en": "The best among you are those who have the best manners and character.",
        "translation_hinglish": "Tum me sab se behtareen wo log hain jinke akhlaaq sab se achhe hain.",
        "translation_hi": "तुममें सबसे बेहतरीन लोग वे हैं जिनके अख़लाक़ (चरित्र) सबसे अच्छे हैं।",
        "translation_ur": "تم میں سب سے بہترین وہ لوگ ہیں جن کے اخلاق سب سے اچھے ہیں۔",
        "reference": "Sahih al-Bukhari 3559",
        "book": "bukhari",
        "number": 3559,
        "grade": "Sahih",
        "topic": "akhlaq"
    },
    {
        "type": "hadith",
        "arabic": "الْمُسْلِمُ مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ",
        "translation_en": "A Muslim is the one from whose tongue and hands the Muslims are safe.",
        "translation_hinglish": "Musalman wo hai jiski zaban aur hath se doosre musalman mehfooz rahein.",
        "translation_hi": "सच्चा मुसलमान वह है जिसकी ज़ुबान और हाथ से दूसरे मुसलमान सुरक्षित रहें।",
        "translation_ur": "مسلمان وہ ہے جس کی زبان اور ہاتھ سے دوسرے مسلمان محفوظ رہیں۔",
        "reference": "Sahih al-Bukhari 10",
        "book": "bukhari",
        "number": 10,
        "grade": "Sahih",
        "topic": "akhlaq"
    },
    {
        "type": "hadith",
        "arabic": "لاَ تَحَاسَدُوا، وَلاَ تَنَاجَشُوا، وَلاَ تَبَاغَضُوا، وَلاَ تَدَابَرُوا، وَكُونُوا عِبَادَ اللَّهِ إِخْوَانًا",
        "translation_en": "Do not envy one another, do not artificially inflate prices, do not hate one another, do not turn away from one another, but be servants of Allah, brothers.",
        "translation_hinglish": "Aapas me hasad na karo, dhoka na do, bughz na rakho, aur Allah ke bande bankar bhai bhai ho jao.",
        "translation_hi": None,
        "translation_ur": "آپس میں حسد نہ کرو، دھوکہ نہ دو، بغض نہ رکھو، ایک دوسرے سے منہ نہ موڑو اور اللہ کے بندے بن کر بھائی بھائی ہو جاؤ۔",
        "reference": "[Muslim 2564]",
        "book": "muslim",
        "number": 2564,
        "grade": "Sahih",
        "topic": "akhlaq"
    },

    # --- PARENTS ---
    {
        "type": "quran",
        "arabic": "وَقَضَىٰ رَبُّكَ أَلَّا تَعْبُدُوا إِلَّا إِيَّاهُ وَبِالْوَالِدَيْنِ إِحْسَانًا",
        "translation_en": "And your Lord has decreed that you not worship except Him, and to parents, good treatment.",
        "translation_hinglish": "Aur tere Rab ne faisla farma diya ke tum Uske siwa kisi ki bandagi na karo aur walidain ke sath husn-e-suluk karo.",
        "translation_hi": "और तेरे रब ने फ़ैसला कर दिया है कि तुम उसके सिवा किसी की इबादत न करो और माता-पिता के साथ अच्छा व्यवहार करो।",
        "translation_ur": "اور آپ کے رب نے فیصلہ فرما دیا ہے کہ تم اس کے سوا کسی کی بندگی نہ کرو اور والدین کے ساتھ حسن سلوک کرو۔",
        "reference": "Surah Al-Isra 17:23",
        "book": "quran",
        "number": 2052,
        "grade": "Mutawatir",
        "topic": "parents"
    },
    {
        "type": "quran",
        "arabic": "وَاخْفِضْ لَهُمَا جَنَاحَ الذُّلِّ مِنَ الرَّحْمَةِ وَقُل رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
        "translation_en": "And lower to them the wing of humility out of mercy and say, 'My Lord, have mercy upon them as they brought me up [when I was] small.'",
        "translation_hinglish": "Aur unke aage rehmat se inkisaari ka bazu jhukaye rakho aur kaho: Aye mere Rab! Un par raham farma jaisa unhone mujhe bachpan me pala.",
        "translation_hi": "और उनके आगे नम्रता के साथ दयालुता का बाज़ू झुकाए रखो और कहो: 'ऐ मेरे रब! उनपर दया कर, जिस प्रकार उन्होंने बचपन में मेरा पालन-पोषण किया।' ",
        "translation_ur": "اور ان کے آگے رحمت سے انکساری کا بازو جھکائے رکھو اور کہو کہ اے میرے رب! ان پر رحم فرما جیسا کہ انہوں نے مجھے بچپن میں پالا۔",
        "reference": "Surah Al-Isra 17:24",
        "book": "quran",
        "number": 2053,
        "grade": "Mutawatir",
        "topic": "parents"
    },
    {
        "type": "hadith",
        "arabic": "جَاءَ رَجُلٌ إِلَى رَسُولِ اللَّهِ صلى الله عليه وسلم فَقَالَ: مَنْ أَحَقُّ النَّاسِ بِحُسْنِ صَحَابَتِي؟ قَالَ: أُمُّكَ. قَالَ: ثُمَّ مَنْ؟ قَالَ: ثُمَّ أُمُّكَ. قَالَ: ثُمَّ مَنْ؟ قَالَ: ثُمَّ أُمُّكَ. قَالَ: ثُمَّ مَنْ؟ قَالَ: ثُمَّ أَبُوكَ",
        "translation_en": "A man came to Allah's Messenger (ﷺ) and said, 'O Allah's Messenger! Who is more entitled to be treated with the best companionship by me?' The Prophet (ﷺ) said, 'Your mother.' The man said. 'Who is next?' The Prophet said, 'Your mother.' The man further said, 'Who is next?' The Prophet said, 'Your mother.' The man asked for the fourth time, 'Who is next?' The Prophet said, 'Your father.'",
        "translation_hinglish": "Ek shakhs ne pucha: Mere achhe suluk ka sab se zyada haqdaar kaun hai? Farmaya: Tumhari maa. Phir kaun? Farmaya: Tumhari maa. Phir kaun? Farmaya: Tumhari maa. Phir kaun? Farmaya: Tumhara baap.",
        "translation_hi": "एक आदमी रसूलुल्लाह (सल्लल्लाहु अलैहि व सल्लम) के पास आया और पूछा: मेरे अच्छे सुलूक का सबसे ज़्यादा हक़दार कौन है? आपने फ़रमाया: तुम्हारी माँ। उसने कहा: फिर कौन? फ़रमाया: तुम्हारी माँ। उसने कहा: फिर कौन? फ़रमाया: तुम्हारी माँ। उसने कहा: फिर कौन? फ़रमाया: फिर तुम्हारा बाप।",
        "translation_ur": "ایک شخص رسول اللہ ﷺ کی خدمت میں حاضر ہوا اور عرض کی: میرے اچھے سلوک کا سب سے زیادہ مستحق کون ہے؟ آپ ﷺ نے فرمایا: تمہاری ماں۔ اس نے کہا: پھر کون؟ فرمایا: تمہاری ماں۔ اس نے کہا: پھر کون؟ فرمایا: تمہاری ماں۔ اس نے پوچھا: پھر کون؟ فرمایا: پھر تمہارا باپ۔",
        "reference": "Sahih al-Bukhari 5971",
        "book": "bukhari",
        "number": 5971,
        "grade": "Sahih",
        "topic": "parents"
    },

    # --- DUA / SUPPLICATION ---
    {
        "type": "quran",
        "arabic": "وَإِذَا سَأَلَكَ عِبَادِي عَنِّي فَإِنِّي قَرِيبٌ ۖ أُجِيبُ دَعْوَةَ الدَّاعِ إِذَا دَعَانِ",
        "translation_en": "And when My servants ask you concerning Me, indeed I am near. I respond to the invocation of the supplicant when he calls upon Me.",
        "translation_hinglish": "Aur jab Mere bande aapse Mere baare me poochein to yaqeenan Main qareeb hoon, pukarne wale ki dua qabool karta hoon.",
        "translation_hi": "और जब मेरे बन्दे आपसे मेरे बारे में पूछें, तो निश्चय ही मैं निकट हूँ। जब कोई पुकारने वाला मुझे पुकारता है, तो मैं उसकी पुकार सुनता हूँ।",
        "translation_ur": "اور جب میرے بندے آپ سے میرے بارے میں پوچھیں تو یقیناً میں قریب ہوں۔ جب کوئی پکارنے والا مجھے پکارتا ہے تو میں اس کی دعا قبول کرتا ہوں۔",
        "reference": "Surah Al-Baqarah 2:186",
        "book": "quran",
        "number": 193,
        "grade": "Mutawatir",
        "topic": "dua"
    },
    {
        "type": "quran",
        "arabic": "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
        "translation_en": "Our Lord, give us in this world [that which is] good and in the Hereafter [that which is] good and protect us from the punishment of the Fire.",
        "translation_hinglish": "Rabbana aatina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina azaban-nar (Aye hamare Rab! Hamein dunya me bhi bhalai de aur aakhirat me bhi bhalai de aur aag ke azaab se bacha).",
        "translation_hi": "ऐ हमारे रब! हमें दुनिया में भी भलाई दे और आख़िरत में भी भलाई दे और हमें आग के अज़ाब से बचा।",
        "translation_ur": "اے ہمارے رب! ہمیں دنیا میں بھی بھلائی دے اور آخرت میں بھی بھلائی دے اور ہمیں آگ کے عذاب سے بچا۔",
        "reference": "Surah Al-Baqarah 2:201",
        "book": "quran",
        "number": 208,
        "grade": "Mutawatir",
        "topic": "dua"
    },
    {
        "type": "hadith",
        "arabic": "أَقْرَبُ مَا يَكُونُ الْعَبْدُ مِنْ رَبِّهِ وَهُوَ سَاجِدٌ فَأَكْثِرُوا الدُّعَاءَ",
        "translation_en": "The closest that a servant comes to his Lord is when he is prostrating himself, so make plenty of supplication.",
        "translation_hinglish": "Banda apne Rab ke sab se zyada qareeb tab hota hai jab wo sajde me ho, pas kasrat se dua kiya karo.",
        "translation_hi": "बन्दा अपने रब के सबसे अधिक निकट उस समय होता है जब वह सजदे में होता है, अतः खूब दुआ किया करो।",
        "translation_ur": "بندہ اپنے رب کے سب سے زیادہ قریب اس وقت ہوتا ہے جب وہ سجدے میں ہو، پس کثرت سے دعا کیا کرو۔",
        "reference": "[Muslim 482]",
        "book": "muslim",
        "number": 482,
        "grade": "Sahih",
        "topic": "dua"
    },

    # --- AKHIRAH / HEREAFTER ---
    {
        "type": "quran",
        "arabic": "كُلُّ نَفْسٍ ذَائِقَةُ الْمَوْتِ ۗ وَإِنَّمَا تُوَفَّوْنَ أُجُورَكُمْ يَوْمَ الْقِيَامَةِ",
        "translation_en": "Every soul will taste death, and you will only be given your full compensation on the Day of Resurrection.",
        "translation_hinglish": "Har jaan ko maut ka maza chakhna hai, aur qiyamat ke din tumhein poora badla diya jayega.",
        "translation_hi": "प्रत्येक जीव को मौत का स्वाद चखना है, और तुम्हें क़ियामत के दिन ही तुम्हारे पूरे-पूरे बदले दिए जाएँगे।",
        "translation_ur": "ہر جان کو موت کا مزہ چکھنا ہے، اور تمہیں قیامت کے دن ہی تمہارے پورے بدلے دیے جائیں گے۔",
        "reference": "Surah Ali 'Imran 3:185",
        "book": "quran",
        "number": 478,
        "grade": "Mutawatir",
        "topic": "akhirah"
    },
    {
        "type": "quran",
        "arabic": "فَمَن يَعْمَلْ مِثْقَالَ ذَرَّةٍ خَيْرًا يَرَهُ ۝ وَمَن يَعْمَلْ مِثْقَالَ ذَرَّةٍ شَرًّا يَرَهُ",
        "translation_en": "So whoever does an atom's weight of good will see it, And whoever does an atom's weight of evil will see it.",
        "translation_hinglish": "Pas jisne zarra barabar neki ki hogi wo use dekh lega, aur jisne zarra barabar burai ki hogi wo use dekh lega.",
        "translation_hi": "अतः जो कोई कण भर भी भलाई करेगा वह उसे देख लेगा, और जो कोई कण भर भी बुराई करेगा वह उसे देख लेगा।",
        "translation_ur": "پس جس نے ذرہ برابر نیکی کی ہوگی وہ اسے دیکھ لے گا، اور جس نے ذرہ برابر برائی کی ہوگی وہ اسے دیکھ لے گا۔",
        "reference": "Surah Az-Zalzalah 99:7-8",
        "book": "quran",
        "number": 6140,
        "grade": "Mutawatir",
        "topic": "akhirah"
    },
    {
        "type": "hadith",
        "arabic": "كُنْ فِي الدُّنْيَا كَأَنَّكَ غَرِيبٌ، أَوْ عَابِرُ سَبِيلٍ",
        "translation_en": "Be in this world as if you were a stranger or a traveler along a path.",
        "translation_hinglish": "Dunya me aise raho jaise tum ajnabi ho ya raah guzarte musafir.",
        "translation_hi": "दुनिया में ऐसे रहो जैसे तुम कोई अजनबी या मुसाफ़िर हो।",
        "translation_ur": "دنیا میں ایسے رہو جیسے تم اجنبی ہو یا راہ گزرتے مسافر۔",
        "reference": "Sahih al-Bukhari 6416",
        "book": "bukhari",
        "number": 6416,
        "grade": "Sahih",
        "topic": "akhirah"
    },

    # --- ILM / KNOWLEDGE ---
    {
        "type": "quran",
        "arabic": "وَقُل رَّبِّ زِدْنِي عِلْمًا",
        "translation_en": "And say, 'My Lord, increase me in knowledge.'",
        "translation_hinglish": "Rabbi zidni 'ilma (Aye mere Rab! Mere ilm me izafa farma).",
        "translation_hi": "और कहो: 'ऐ मेरे रब! मेरे ज्ञान में वृद्धि कर।' ",
        "translation_ur": "اور کہو کہ اے میرے رب! میرے علم میں اضافہ فرما۔",
        "reference": "Surah Taha 20:114",
        "book": "quran",
        "number": 2462,
        "grade": "Mutawatir",
        "topic": "ilm"
    },
    {
        "type": "hadith",
        "arabic": "مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ",
        "translation_en": "Whoever takes a path upon which he seeks knowledge, Allah makes the path to Paradise easy for him.",
        "translation_hinglish": "Jo shakhs ilm ki talash me kisi raste par chalta hai, Allah uske liye jannat ka rasta aasan kar deta hai.",
        "translation_hi": "जो व्यक्ति ज्ञान प्राप्त करने के मार्ग पर चलता है, अल्लाह उसके लिए जन्नत का मार्ग आसान कर देता है।",
        "translation_ur": "جو شخص علم کی تلاش میں کسی راستے پر چلتا ہے، اللہ اس کے لیے جنت کا راستہ آسان فرما دیتا ہے۔",
        "reference": "[Muslim 2699]",
        "book": "muslim",
        "number": 2699,
        "grade": "Sahih",
        "topic": "ilm"
    },
    {
        "type": "hadith",
        "arabic": "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
        "translation_en": "The best among you are those who learn the Qur'an and teach it.",
        "translation_hinglish": "Tum me se sab se behtar wo hai jo Quran seekhe aur doosron ko sikhaye.",
        "translation_hi": "तुममें सबसे उत्तम वह है जो क़ुरआन सीखे और दूसरों को सिखाए।",
        "translation_ur": "تم میں سب سے بہترین وہ ہے جو قرآن سیکھے اور اسے سکھائے۔",
        "reference": "Sahih al-Bukhari 5027",
        "book": "bukhari",
        "number": 5027,
        "grade": "Sahih",
        "topic": "ilm"
    },

    # --- CHARITY / SADAQAH ---
    {
        "type": "quran",
        "arabic": "مَّثَلُ الَّذِينَ يُنفِقُونَ أَمْوَالَهُمْ فِي سَبِيلِ اللَّهِ كَمَثَلِ حَبَّةٍ أَنبَتَتْ سَبْعَ سَنَابِلَ فِي كُلِّ سُنبُلَةٍ مِّائَةُ حَبَّةٍ",
        "translation_en": "The example of those who spend their wealth in the way of Allah is like a seed of grain which grows seven spikes; in each spike is a hundred grains.",
        "translation_hinglish": "Jo log apna maal Allah ki raah me kharch karte hain unki misaal ek daane jaisi hai jisse 7 baaliyan ugein, har baali me 100 daane hon.",
        "translation_hi": "जो लोग अपने माल अल्लाह के मार्ग में ख़र्च करते हैं, उनका उदाहरण एक दाने जैसा है जिससे सात बालियाँ उगीं, प्रत्येक बाली में सौ दाने हों।",
        "translation_ur": "جو لوگ اپنے مال اللہ کی راہ میں خرچ کرتے ہیں ان کی مثال ایک دانے کی سی ہے جس سے سات بالیاں اگیں، ہر بالی میں سو دانے ہوں۔",
        "reference": "Surah Al-Baqarah 2:261",
        "book": "quran",
        "number": 268,
        "grade": "Mutawatir",
        "topic": "charity"
    },
    {
        "type": "hadith",
        "arabic": "كُلُّ مَعْرُوفٍ صَدَقَةٌ",
        "translation_en": "Every good deed is charity.",
        "translation_hinglish": "Har neki ka kaam sadqa hai.",
        "translation_hi": "प्रत्येक भलाई का कार्य सदक़ा है।",
        "translation_ur": "ہر نیکی کا کام صدقہ ہے۔",
        "reference": "Sahih al-Bukhari 6021",
        "book": "bukhari",
        "number": 6021,
        "grade": "Sahih",
        "topic": "charity"
    },
    {
        "type": "hadith",
        "arabic": "مَا نَقَصَتْ صَدَقَةٌ مِنْ مَالٍ وَمَا زَادَ اللَّهُ عَبْدًا بِعَفْوٍ إِلاَّ عِزًّا",
        "translation_en": "Charity does not decrease wealth, and no one forgives another except that Allah increases his honor.",
        "translation_hinglish": "Sadqa maal me kami nahi karta, aur maaf karne wale ki Allah izzat hi badhata hai.",
        "translation_hi": None,
        "translation_ur": "صدقہ مال میں کمی نہیں کرتا، اور معاف کرنے والے کی اللہ عزت ہی بڑھاتا ہے۔",
        "reference": "[Muslim 2588]",
        "book": "muslim",
        "number": 2588,
        "grade": "Sahih",
        "topic": "charity"
    },

    # --- FAMILY / KINSHIP ---
    {
        "type": "quran",
        "arabic": "وَاتَّقُوا اللَّهَ الَّذِي تَسَاءَلُونَ بِهِ وَالْأَرْحَامَ ۚ إِنَّ اللَّهَ كَانَ عَلَيْكُمْ رَقِيبًا",
        "translation_en": "And fear Allah, through whom you ask one another, and the wombs. Indeed Allah is ever, over you, an Observer.",
        "translation_hinglish": "Aur us Allah se daro jiske waaste se tum ek doosre se maangte ho aur rishton ke haqooq ka khayal rakho.",
        "translation_hi": "और उस अल्लाह से डरो जिसके नाम से तुम एक-दूसरे से मांगते हो, और रिश्तों (के अधिकारों) का ध्यान रखो। निश्चय ही अल्लाह तुम पर निगरानी रख रहा है।",
        "translation_ur": "اور اس اللہ سے ڈرو جس کے واسطے سے تم ایک دوسرے سے سوال کرتے ہو، اور رشتوں کے حقوق کا خیال رکھو۔ بے شک اللہ تم پر نگہبان ہے۔",
        "reference": "Surah An-Nisa 4:1",
        "book": "quran",
        "number": 494,
        "grade": "Mutawatir",
        "topic": "family"
    },
    {
        "type": "hadith",
        "arabic": "مَنْ سَرَّهُ أَنْ يُبْسَطَ لَهُ فِي رِزْقِهِ، أَوْ يُنْسَأَ لَهُ فِي أَثَرِهِ، فَلْيَصِلْ رَحِمَهُ",
        "translation_en": "Whoever loves that he be granted more wealth and that his lease of life be prolonged, then he should keep good relations with his kith and kin.",
        "translation_hinglish": "Jo shakhs chahta hai ke uske rizq me kushadgi aur umar me barkat ho, to use chahiye ke sila rehmi kare.",
        "translation_hi": "जो यह पसंद करता है कि उसकी रोज़ी में बरकत हो और उसकी उम्र लंबी हो, उसे चाहिए कि वह नाते-रिश्तेदारों के साथ अच्छा सुलूक करे।",
        "translation_ur": "جو شخص چاہتا ہے کہ اس کے رزق میں کشادگی اور عمر میں برکت ہو، تو اسے چاہیے کہ صلہ رحمی کرے (رشتہ داروں سے اچھا سلوک کرے)۔",
        "reference": "Sahih al-Bukhari 2067",
        "book": "bukhari",
        "number": 2067,
        "grade": "Sahih",
        "topic": "family"
    },
    {
        "type": "hadith",
        "arabic": "لاَ يَدْخُلُ الْجَنَّةَ قَاطِعُ رَحِمٍ",
        "translation_en": "The one who severs the ties of kinship will not enter Paradise.",
        "translation_hinglish": "Rishta todne wala (qata-rehmi karne wala) jannat me dakhil nahi hoga.",
        "translation_hi": "रिश्ता तोड़ने वाला जन्नत में दाख़िल नहीं होगा।",
        "translation_ur": "قطع رحمی کرنے والا (رشتہ توڑنے والا) جنت میں داخل نہیں ہوگا۔",
        "reference": "[Muslim 2556]",
        "book": "muslim",
        "number": 2556,
        "grade": "Sahih",
        "topic": "family"
    }
]

def main():
    os.makedirs(DB_DIR, exist_ok=True)
    if os.path.exists(DB_PATH):
        os.remove(DB_PATH)

    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()

    cur.execute("""
    CREATE TABLE IF NOT EXISTS `content_items` (
        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
        `type` TEXT NOT NULL,
        `arabic` TEXT NOT NULL,
        `translation_en` TEXT NOT NULL,
        `translation_hinglish` TEXT,
        `translation_hi` TEXT,
        `translation_ur` TEXT,
        `reference` TEXT NOT NULL,
        `book` TEXT NOT NULL,
        `number` INTEGER NOT NULL,
        `grade` TEXT,
        `topic` TEXT NOT NULL,
        `word_count` INTEGER NOT NULL,
        `is_favorite` INTEGER NOT NULL DEFAULT 0
    );
    """)

    cur.execute("CREATE INDEX IF NOT EXISTS `index_content_items_type_topic_word_count` ON `content_items` (`type`, `topic`, `word_count`);")
    cur.execute("CREATE UNIQUE INDEX IF NOT EXISTS `index_content_items_book_number_type` ON `content_items` (`book`, `number`, `type`);")

    for item in ITEMS:
        arabic = item["arabic"].strip()
        trans_en = item["translation_en"].strip()
        trans_hinglish = item.get("translation_hinglish")
        if trans_hinglish:
            trans_hinglish = trans_hinglish.strip()

        trans_hi = item.get("translation_hi")
        if trans_hi:
            trans_hi = trans_hi.strip()

        trans_ur = item.get("translation_ur")
        if trans_ur:
            trans_ur = trans_ur.strip()

        w_count = count_words(arabic)

        cur.execute("""
        INSERT INTO `content_items` (
            `type`, `arabic`, `translation_en`, `translation_hinglish`, `translation_hi`, `translation_ur`,
            `reference`, `book`, `number`, `grade`, `topic`, `word_count`, `is_favorite`
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)
        """, (
            item["type"],
            arabic,
            trans_en,
            trans_hinglish,
            trans_hi,
            trans_ur,
            item["reference"].strip(),
            item["book"].strip(),
            item["number"],
            item.get("grade"),
            item["topic"].strip(),
            w_count
        ))

    conn.commit()
    cur.execute("SELECT COUNT(*) FROM `content_items`")
    total_count = cur.fetchone()[0]
    conn.close()

    print("========================================")
    print("Database built successfully with Hinglish at:", DB_PATH)
    print(f"Total entries: {total_count}")
    print("========================================")

if __name__ == "__main__":
    main()
