package com.example.data

import com.example.model.DifficultyLevel
import com.example.model.WordCategory
import com.example.model.WordItem

object VocabularyRepository {

    val allWords: List<WordItem> = listOf(
        // Animals (प्राणी)
        WordItem(
            id = "lion",
            word = "Lion",
            phonetic = "LY-uhn",
            marathiMeaning = "सिंह",
            marathiPronounce = "लायन",
            category = WordCategory.ANIMALS,
            emoji = "🦁",
            exampleSentence = "The lion is the king of the jungle.",
            marathiSentence = "सिंह जंगलाचा राजा आहे.",
            funFact = "Lions roar so loudly it can be heard 8 km away! सिंहाची गर्जना ८ किमी लांबून ऐकू येते!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "elephant",
            word = "Elephant",
            phonetic = "EL-uh-fuhnt",
            marathiMeaning = "हत्ती",
            marathiPronounce = "एलिफंट",
            category = WordCategory.ANIMALS,
            emoji = "🐘",
            exampleSentence = "An elephant has a long trunk.",
            marathiSentence = "हत्तीला लांब सोंड असते.",
            funFact = "Elephants can swim and use their trunk like a snorkel! हत्ती सोंडेच्या साहाय्याने पाण्यात श्वास घेऊ शकतात!",
            difficulty = DifficultyLevel.MEDIUM
        ),
        WordItem(
            id = "monkey",
            word = "Monkey",
            phonetic = "MUHNG-kee",
            marathiMeaning = "माकड",
            marathiPronounce = "मंकी",
            category = WordCategory.ANIMALS,
            emoji = "🐒",
            exampleSentence = "The monkey loves eating yellow bananas.",
            marathiSentence = "माकडाला पिवळी केळी खायला आवडतात.",
            funFact = "Monkeys swing swiftly from tree to tree! माकडे झाडावर खूप वेगाने उड्या मारतात!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "tiger",
            word = "Tiger",
            phonetic = "TY-ger",
            marathiMeaning = "वाघ",
            marathiPronounce = "टायगर",
            category = WordCategory.ANIMALS,
            emoji = "🐅",
            exampleSentence = "A tiger has orange and black stripes.",
            marathiSentence = "वाघाच्या अंगावर काळे आणि केशरी पट्टे असतात.",
            funFact = "No two tigers have the exact same stripes! दोन वाघांचे पट्टे कधीही सारखे नसतात!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "rabbit",
            word = "Rabbit",
            phonetic = "RAB-it",
            marathiMeaning = "ससा",
            marathiPronounce = "रॅबिट",
            category = WordCategory.ANIMALS,
            emoji = "🐰",
            exampleSentence = "The fluffy rabbit hops fast.",
            marathiSentence = "गुटगुटीत ससा वेगाने उड्या मारतो.",
            funFact = "Rabbits love to munch on crunchy carrots! सशांना ताजे गाजर खायला खूप आवडते!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "dog",
            word = "Dog",
            phonetic = "DOG",
            marathiMeaning = "कुत्रा",
            marathiPronounce = "डॉग",
            category = WordCategory.ANIMALS,
            emoji = "🐶",
            exampleSentence = "The dog wags its tail happily.",
            marathiSentence = "कुत्रा आनंदाने शेपूट हलवतो.",
            funFact = "Dogs are our best and loyal friends! कुत्रे माणसाचे सर्वात विश्वासू मित्र असतात!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "cat",
            word = "Cat",
            phonetic = "KAT",
            marathiMeaning = "मांजर",
            marathiPronounce = "कॅट",
            category = WordCategory.ANIMALS,
            emoji = "🐱",
            exampleSentence = "The cat drinks fresh warm milk.",
            marathiSentence = "मांजर ताजे कोमट दूध पिते.",
            funFact = "Cats can jump up to 6 times their height! मांजरी त्यांच्या उंचीच्या ६ पट उंच उडी मारू शकतात!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "duck",
            word = "Duck",
            phonetic = "DUHK",
            marathiMeaning = "बदक",
            marathiPronounce = "डक",
            category = WordCategory.ANIMALS,
            emoji = "🦆",
            exampleSentence = "The duck swims in the pond saying quack quack.",
            marathiSentence = "बदक तलावात क्वाक-क्वाक करत पोहते.",
            funFact = "Duck feathers are waterproof! बदकाची पिसे पाण्याने ओली होत नाहीत!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "giraffe",
            word = "Giraffe",
            phonetic = "juh-RAF",
            marathiMeaning = "जिराफ",
            marathiPronounce = "जिराफ",
            category = WordCategory.ANIMALS,
            emoji = "🦒",
            exampleSentence = "The giraffe reaches high leaves with its neck.",
            marathiSentence = "जिराफ आपल्या लांब मानेने उंच झाडांची पाने खातो.",
            funFact = "Giraffes are the tallest animals on Earth! जिराफ हा पृथ्वीवरील सर्वात उंच प्राणी आहे!",
            difficulty = DifficultyLevel.MEDIUM
        ),
        WordItem(
            id = "zebra",
            word = "Zebra",
            phonetic = "ZEE-bruh",
            marathiMeaning = "झिब्रा",
            marathiPronounce = "झिब्रा",
            category = WordCategory.ANIMALS,
            emoji = "🦓",
            exampleSentence = "The zebra looks like a striped horse.",
            marathiSentence = "झिब्रा पट्टेरी घोड्यासारखा दिसतो.",
            funFact = "Zebras stand up even while sleeping! झिब्रा उभे राहूनही झोपू शकतात!",
            difficulty = DifficultyLevel.MEDIUM
        ),

        // Fruits & Vegetables (फळे आणि भाज्या)
        WordItem(
            id = "apple",
            word = "Apple",
            phonetic = "AP-uhl",
            marathiMeaning = "सफरचंद",
            marathiPronounce = "ॲपल",
            category = WordCategory.FRUITS_VEGGIES,
            emoji = "🍎",
            exampleSentence = "An apple a day keeps the doctor away.",
            marathiSentence = "रोज एक सफरचंद खाल्ल्यास आजारपण दूर राहते.",
            funFact = "Apples can float on water because they are 25% air! सफरचंद पाण्यावर तरंगतात!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "mango",
            word = "Mango",
            phonetic = "MANG-goh",
            marathiMeaning = "आंबा",
            marathiPronounce = "मँगो",
            category = WordCategory.FRUITS_VEGGIES,
            emoji = "🥭",
            exampleSentence = "Mango is the sweet king of fruits.",
            marathiSentence = "आंबा हा फळांचा गोड राजा आहे.",
            funFact = "Mango is India's national fruit! आंबा हा भारताचा राष्ट्रीय फळ आहे!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "banana",
            word = "Banana",
            phonetic = "buh-NAN-uh",
            marathiMeaning = "केळे",
            marathiPronounce = "बनाना",
            category = WordCategory.FRUITS_VEGGIES,
            emoji = "🍌",
            exampleSentence = "Peel the yellow banana before eating.",
            marathiSentence = "खाण्यापूर्वी पिवळ्या केळ्याची साल काढा.",
            funFact = "Bananas give instant energy for running and playing! केळी खाल्ल्याने झटपट ताकद मिळते!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "orange",
            word = "Orange",
            phonetic = "OR-inj",
            marathiMeaning = "संत्रे",
            marathiPronounce = "ऑरेंज",
            category = WordCategory.FRUITS_VEGGIES,
            emoji = "🍊",
            exampleSentence = "Orange juice is tasty and full of Vitamin C.",
            marathiSentence = "संत्र्याचा रस चविष्ट आणि जीवनसत्त्वांनी भरलेला असतो.",
            funFact = "Oranges keep our teeth and gums healthy! संत्री आपल्या हिरड्या निरोगी ठेवतात!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "strawberry",
            word = "Strawberry",
            phonetic = "STRAW-ber-ee",
            marathiMeaning = "स्ट्रॉबेरी",
            marathiPronounce = "स्ट्रॉबेरी",
            category = WordCategory.FRUITS_VEGGIES,
            emoji = "🍓",
            exampleSentence = "The red strawberry is sweet and juicy.",
            marathiSentence = "लाल स्ट्रॉबेरी गोड आणि रसाळ असते.",
            funFact = "Strawberries wear their seeds on the outside! स्ट्रॉबेरीच्या बिया फळाच्या बाहेर असतात!",
            difficulty = DifficultyLevel.MEDIUM
        ),
        WordItem(
            id = "carrot",
            word = "Carrot",
            phonetic = "KAIR-uht",
            marathiMeaning = "गाजर",
            marathiPronounce = "कॅरट",
            category = WordCategory.FRUITS_VEGGIES,
            emoji = "🥕",
            exampleSentence = "Eating carrots makes eyesight sharp.",
            marathiSentence = "गाजर खाल्ल्याने डोळे तीक्ष्ण राहतात.",
            funFact = "Carrots grow under the cool soil! गाजर जमिनीखाली वाढतात!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "watermelon",
            word = "Watermelon",
            phonetic = "WAW-ter-mel-uhn",
            marathiMeaning = "कलिंगड / टरबूज",
            marathiPronounce = "वॉटरमेलन",
            category = WordCategory.FRUITS_VEGGIES,
            emoji = "🍉",
            exampleSentence = "Watermelon is delicious and refreshing in summer.",
            marathiSentence = "उन्हाळ्यात थंड कलिंगड खाणे खूप छान वाटते.",
            funFact = "Watermelon is 92% water! कलिंगडामध्ये ९२% पाणी असते!",
            difficulty = DifficultyLevel.MEDIUM
        ),

        // Colors & Shapes (रंग आणि आकार)
        WordItem(
            id = "red",
            word = "Red",
            phonetic = "RED",
            marathiMeaning = "लाल रंग",
            marathiPronounce = "रेड",
            category = WordCategory.COLORS_SHAPES,
            emoji = "🔴",
            exampleSentence = "The shiny ripe tomato is red.",
            marathiSentence = "पिकलेला टोमॅटो लाल रंगाचा असतो.",
            funFact = "Red is the color of fire trucks and roses! लाल रंग गुलाबाचा आणि अग्निशामक गाडीचा असतो!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "blue",
            word = "Blue",
            phonetic = "BLOO",
            marathiMeaning = "निळा रंग",
            marathiPronounce = "ब्लू",
            category = WordCategory.COLORS_SHAPES,
            emoji = "🔵",
            exampleSentence = "The open sky and deep ocean are blue.",
            marathiSentence = "मोकळे आकाश आणि खोल समुद्र निळा असतो.",
            funFact = "Blue is the world's most popular favorite color! निळा रंग जगातील बहुतेकांचा आवडता रंग आहे!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "green",
            word = "Green",
            phonetic = "GREEN",
            marathiMeaning = "हिरवा रंग",
            marathiPronounce = "ग्रीन",
            category = WordCategory.COLORS_SHAPES,
            emoji = "🟢",
            exampleSentence = "The garden grass is fresh and green.",
            marathiSentence = "बागेतील गवत ताजे आणि हिरवेगार आहे.",
            funFact = "Green makes eyes feel calm and happy! हिरवा रंग पाहिल्याने डोळ्यांना शांती मिळते!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "yellow",
            word = "Yellow",
            phonetic = "YEL-oh",
            marathiMeaning = "पिवळा रंग",
            marathiPronounce = "यलो",
            category = WordCategory.COLORS_SHAPES,
            emoji = "🟡",
            exampleSentence = "The warm morning sun is bright yellow.",
            marathiSentence = "सकाळचा कोवळा सूर्य चमकदार पिवळा असतो.",
            funFact = "Sunflowers turn to follow the yellow sun! सूर्यफुले पिवळ्या सूर्याकडे तोंड फिरवतात!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "star",
            word = "Star",
            phonetic = "STAHR",
            marathiMeaning = "चांदणी / तारा",
            marathiPronounce = "स्टार",
            category = WordCategory.COLORS_SHAPES,
            emoji = "⭐",
            exampleSentence = "Twinkle, twinkle, little star!",
            marathiSentence = "आकाशात रात्री छान चमचमणाऱ्या चांदण्या दिसतात.",
            funFact = "Stars look tiny, but they are huge balls of light! चांदण्या खूप मोठ्या असतात!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "circle",
            word = "Circle",
            phonetic = "SUR-kuhl",
            marathiMeaning = "वर्तुळ / गोल",
            marathiPronounce = "सर्कल",
            category = WordCategory.COLORS_SHAPES,
            emoji = "⭕",
            exampleSentence = "A round coin and wheel are shaped like a circle.",
            marathiSentence = "नाणे आणि सायकलचे चाक गोलाकार वर्तुळ असते.",
            funFact = "A circle has zero straight sides or corners! वर्तुळाला एकही कोपरा नसतो!",
            difficulty = DifficultyLevel.MEDIUM
        ),
        WordItem(
            id = "heart",
            word = "Heart",
            phonetic = "HAHRT",
            marathiMeaning = "हृदय / प्रेमाचे चिन्ह",
            marathiPronounce = "हार्ट",
            category = WordCategory.COLORS_SHAPES,
            emoji = "💖",
            exampleSentence = "The red heart shows love and kindness.",
            marathiSentence = "हार्ट हे प्रेम आणि आपुलकीचे सुंदर प्रतीक आहे.",
            funFact = "Your heart beats about 100 times every minute! लहान मुलांचे हृदय मिनिटाला १०० वेळा धडकते!",
            difficulty = DifficultyLevel.EASY
        ),

        // Vehicles (वाहने)
        WordItem(
            id = "car",
            word = "Car",
            phonetic = "KAHR",
            marathiMeaning = "गाडी / मोटार",
            marathiPronounce = "कार",
            category = WordCategory.VEHICLES,
            emoji = "🚗",
            exampleSentence = "Dad drives the red car carefully.",
            marathiSentence = "बाबा काळजीपूर्वक लाल कार चालवतात.",
            funFact = "Cars have seatbelts to keep everyone safe! कारमध्ये सुरक्षेसाठी सीटबेल्ट असतो!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "train",
            word = "Train",
            phonetic = "TRAYN",
            marathiMeaning = "आगगाडी / रेल्वे",
            marathiPronounce = "ट्रेन",
            category = WordCategory.VEHICLES,
            emoji = "🚆",
            exampleSentence = "The train chugs along the steel tracks.",
            marathiSentence = "आगगाडी रुळांवरून झुकझुक करत धावते.",
            funFact = "Trains can carry thousands of happy travelers! ट्रेन हजारो प्रवाशांना एकत्र घेऊन जाते!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "aeroplane",
            word = "Aeroplane",
            phonetic = "AIR-uh-playn",
            marathiMeaning = "विमान",
            marathiPronounce = "एरोप्लेन",
            category = WordCategory.VEHICLES,
            emoji = "✈️",
            exampleSentence = "The aeroplane flies high above the white clouds.",
            marathiSentence = "विमान पांढऱ्या ढगांच्या वर उंच उडते.",
            funFact = "Aeroplanes can fly faster than birds! विमाने पक्ष्यांपेक्षाही खूप वेगाने उडू शकतात!",
            difficulty = DifficultyLevel.MEDIUM
        ),
        WordItem(
            id = "bicycle",
            word = "Bicycle",
            phonetic = "BY-si-kuhl",
            marathiMeaning = "सायकल",
            marathiPronounce = "बायसिकल",
            category = WordCategory.VEHICLES,
            emoji = "🚲",
            exampleSentence = "I ring the bell on my bicycle: tring tring!",
            marathiSentence = "मी माझ्या सायकलची घंटा वाजवतो: ट्रिंग ट्रिंग!",
            funFact = "Riding a bicycle keeps you fit and produces zero smoke! सायकल चालवणे आरोग्यासाठी उत्तम असते!",
            difficulty = DifficultyLevel.MEDIUM
        ),
        WordItem(
            id = "rocket",
            word = "Rocket",
            phonetic = "ROK-it",
            marathiMeaning = "रॉकेट / अंतराळयान",
            marathiPronounce = "रॉकेट",
            category = WordCategory.VEHICLES,
            emoji = "🚀",
            exampleSentence = "The rocket blasts off to the moon.",
            marathiSentence = "रॉकेट आकाशात चंद्राकडे झेपावते.",
            funFact = "Rockets travel at over 28,000 km per hour! रॉकेट अंतराळात प्रचंड वेगाने जाते!",
            difficulty = DifficultyLevel.MEDIUM
        ),

        // School & Home (शाळा आणि घर)
        WordItem(
            id = "book",
            word = "Book",
            phonetic = "BOOK",
            marathiMeaning = "पुस्तक",
            marathiPronounce = "बुक",
            category = WordCategory.EVERYDAY_SCHOOL,
            emoji = "📖",
            exampleSentence = "Open your book to read an interesting story.",
            marathiSentence = "मजेदार गोष्ट वाचण्यासाठी पुस्तक उघडा.",
            funFact = "Books open doors to magical adventures! पुस्तके वाचल्याने नवीन गोष्टी शिकता येतात!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "pencil",
            word = "Pencil",
            phonetic = "PEN-suhl",
            marathiMeaning = "पेन्सिल",
            marathiPronounce = "पेन्सिल",
            category = WordCategory.EVERYDAY_SCHOOL,
            emoji = "✏️",
            exampleSentence = "Draw a smiling face with your sharp pencil.",
            marathiSentence = "पेन्सिलने कागदावर हसरा चेहरा काढा.",
            funFact = "One pencil can draw a line 56 kilometers long! एका पेन्सिलने ५६ किमी लांब रेघ ओढता येते!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "ball",
            word = "Ball",
            phonetic = "BAWL",
            marathiMeaning = "चेंडू",
            marathiPronounce = "बॉल",
            category = WordCategory.EVERYDAY_SCHOOL,
            emoji = "⚽",
            exampleSentence = "Kick the ball into the goal post.",
            marathiSentence = "चेंडू लाथ मारून गोलपोस्टमध्ये टाका.",
            funFact = "Playing ball games improves focus and balance! चेंडू खेळल्याने शरीराचा तोल चांगला राहतो!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "sun",
            word = "Sun",
            phonetic = "SUHN",
            marathiMeaning = "सूर्य",
            marathiPronounce = "सन",
            category = WordCategory.EVERYDAY_SCHOOL,
            emoji = "☀️",
            exampleSentence = "The sun rises in the east and gives us light.",
            marathiSentence = "सूर्य पूर्वेला उगवतो आणि आपल्याला प्रकाश देतो.",
            funFact = "Sunlight gives us healthy Vitamin D! सकाळच्या कोवळ्या उन्हातून व्हिटॅमिन डी मिळते!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "moon",
            word = "Moon",
            phonetic = "MOON",
            marathiMeaning = "चंद्र",
            marathiPronounce = "मून",
            category = WordCategory.EVERYDAY_SCHOOL,
            emoji = "🌙",
            exampleSentence = "The silver moon shines softly at night.",
            marathiSentence = "रात्री आकाशात रूपेरी चंद्र शांतपणे चमकतो.",
            funFact = "The moon changes its shape every night! चंद्र रोज आपला आकार बदलतो!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "tree",
            word = "Tree",
            phonetic = "TREE",
            marathiMeaning = "झाड / वृक्ष",
            marathiPronounce = "ट्री",
            category = WordCategory.EVERYDAY_SCHOOL,
            emoji = "🌳",
            exampleSentence = "The big tree gives sweet shade and cool air.",
            marathiSentence = "मोठे झाड आपल्याला थंडगार सावली आणि शुद्ध हवा देते.",
            funFact = "Trees make oxygen for all of us to breathe! झाडे आपल्याला श्वास घेण्यासाठी प्राणवायू देतात!",
            difficulty = DifficultyLevel.EASY
        ),

        // Action Words (कृती आणि हालचाली)
        WordItem(
            id = "jump",
            word = "Jump",
            phonetic = "JUHMP",
            marathiMeaning = "उडी मारणे",
            marathiPronounce = "जम्प",
            category = WordCategory.ACTIONS,
            emoji = "🦘",
            exampleSentence = "Jump high like a playful kangaroo!",
            marathiSentence = "कांगारूसारखी उंच उडी मारा!",
            funFact = "Jumping makes your leg bones strong! उड्या मारल्याने पायांचे स्नायू बळकट होतात!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "run",
            word = "Run",
            phonetic = "RUHN",
            marathiMeaning = "पळणे / धावणे",
            marathiPronounce = "रन",
            category = WordCategory.ACTIONS,
            emoji = "🏃",
            exampleSentence = "Run fast to win the race.",
            marathiSentence = "शर्यत जिंकण्यासाठी वेगाने पळा.",
            funFact = "Running with friends is super fun! मित्रांसोबत पळणे खूप आनंददायी असते!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "smile",
            word = "Smile",
            phonetic = "SMYL",
            marathiMeaning = "हसणे / स्मितहास्य",
            marathiPronounce = "स्माईल",
            category = WordCategory.ACTIONS,
            emoji = "😊",
            exampleSentence = "Always smile when you meet someone new.",
            marathiSentence = "नव्या व्यक्तीला भेटताना नेहमी गोड हसा.",
            funFact = "A smile is understood in every language on Earth! हसू ही जगातील प्रत्येक माणसाला समजणारी भाषा आहे!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "dance",
            word = "Dance",
            phonetic = "DANS",
            marathiMeaning = "नाचणे / नृत्य करणे",
            marathiPronounce = "डान्स",
            category = WordCategory.ACTIONS,
            emoji = "💃",
            exampleSentence = "We dance to the joyful music.",
            marathiSentence = "आनंददायी संगीतावर आपण छान नाचतो.",
            funFact = "Dancing fills our heart with joy! नाचल्याने मन प्रसन्न आणि आनंदी होते!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "clap",
            word = "Clap",
            phonetic = "KLAP",
            marathiMeaning = "टाळी वाजवणे",
            marathiPronounce = "क्लॅप",
            category = WordCategory.ACTIONS,
            emoji = "👏",
            exampleSentence = "Clap your hands if you are happy!",
            marathiSentence = "आनंदी असाल तर दोन्ही हातांनी टाळी वाजवा!",
            funFact = "Clapping stimulates acupressure points in hands! टाळी वाजवल्याने हातातील नसांना व्यायाम मिळतो!",
            difficulty = DifficultyLevel.EASY
        ),
        WordItem(
            id = "sleep",
            word = "Sleep",
            phonetic = "SLEEP",
            marathiMeaning = "झोपणे",
            marathiPronounce = "स्लीप",
            category = WordCategory.ACTIONS,
            emoji = "😴",
            exampleSentence = "Get good sleep to wake up fresh and energetic.",
            marathiSentence = "सकाळी ताजेतवाने उठण्यासाठी शांत झोप घ्या.",
            funFact = "While sleeping, our brain saves all new words we learned! झोपेत मेंदू दिवसभरात शिकलेले शब्द साठवून ठेवतो!",
            difficulty = DifficultyLevel.EASY
        )
    )

    fun getWordsByCategory(category: WordCategory): List<WordItem> {
        return allWords.filter { it.category == category }
    }

    fun getWordById(id: String): WordItem {
        return allWords.find { it.id == id } ?: allWords.first()
    }

    fun getRandomWord(): WordItem {
        return allWords.random()
    }

    fun getQuizOptions(correctWord: WordItem, count: Int = 4): List<WordItem> {
        val sameCategory = allWords.filter { it.category == correctWord.category && it.id != correctWord.id }
        val others = allWords.filter { it.id != correctWord.id && it.category != correctWord.category }
        val pool = (sameCategory.shuffled() + others.shuffled()).take(count - 1)
        return (pool + correctWord).shuffled()
    }
}
