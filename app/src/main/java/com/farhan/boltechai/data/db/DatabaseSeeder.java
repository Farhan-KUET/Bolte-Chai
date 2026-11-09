package com.farhan.boltechai.data.db;

import com.farhan.boltechai.AppConstants;
import com.farhan.boltechai.data.db.dao.TileDao;
import com.farhan.boltechai.data.db.entity.TileEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseSeeder — Seeds the database with default Bangla picture tiles.
 *
 * Lab Topic: Room database
 *
 * Creates all built-in tiles for the 8 categories:
 * food, needs, feelings, family, school, market, mosque, hospital.
 * Each tile has both Bangla and English labels.
 */
public final class DatabaseSeeder {

    private DatabaseSeeder() {} // Prevent instantiation

    /**
     * Seeds the tiles table with default data for all categories.
     * Called once when the database is first created.
     */
    public static void seedDefaultTiles(TileDao tileDao) {
        List<TileEntity> tiles = new ArrayList<>();

        // ===== Food (খাবার) =====
        int order = 0;
        tiles.add(new TileEntity(AppConstants.CAT_FOOD, "ভাত", "Rice", "ic_rice", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FOOD, "পানি", "Water", "ic_water", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FOOD, "দুধ", "Milk", "ic_milk", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FOOD, "রুটি", "Bread", "ic_bread", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FOOD, "ডাল", "Lentils", "ic_lentils", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FOOD, "মাছ", "Fish", "ic_fish", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FOOD, "মুরগি", "Chicken", "ic_chicken", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FOOD, "ফল", "Fruit", "ic_fruit", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FOOD, "চা", "Tea", "ic_tea", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FOOD, "বিস্কুট", "Biscuit", "ic_biscuit", false, order++));

        // ===== Needs (প্রয়োজন) =====
        order = 0;
        tiles.add(new TileEntity(AppConstants.CAT_NEEDS, "টয়লেট", "Toilet", "ic_toilet", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_NEEDS, "ঘুম", "Sleep", "ic_sleep", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_NEEDS, "সাহায্য", "Help", "ic_help", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_NEEDS, "ব্যথা", "Pain", "ic_pain", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_NEEDS, "ওষুধ", "Medicine", "ic_medicine", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_NEEDS, "কাপড়", "Clothes", "ic_clothes", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_NEEDS, "জুতা", "Shoes", "ic_shoes", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_NEEDS, "চশমা", "Glasses", "ic_glasses", false, order++));

        // ===== Feelings (অনুভূতি) =====
        order = 0;
        tiles.add(new TileEntity(AppConstants.CAT_FEELINGS, "খুশি", "Happy", "ic_happy", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FEELINGS, "দুঃখ", "Sad", "ic_sad", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FEELINGS, "রাগ", "Angry", "ic_angry", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FEELINGS, "ভয়", "Fear", "ic_fear", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FEELINGS, "ক্লান্ত", "Tired", "ic_tired", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FEELINGS, "ভালো", "Good", "ic_good", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FEELINGS, "খারাপ", "Bad", "ic_bad", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FEELINGS, "ভালোবাসা", "Love", "ic_love", false, order++));

        // ===== Family (পরিবার) =====
        order = 0;
        tiles.add(new TileEntity(AppConstants.CAT_FAMILY, "মা", "Mother", "ic_mother", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FAMILY, "বাবা", "Father", "ic_father", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FAMILY, "ভাই", "Brother", "ic_brother", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FAMILY, "বোন", "Sister", "ic_sister", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FAMILY, "দাদা", "Grandfather", "ic_grandfather", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FAMILY, "দাদি", "Grandmother", "ic_grandmother", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FAMILY, "নানা", "Maternal Grandfather", "ic_nana", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_FAMILY, "নানি", "Maternal Grandmother", "ic_nani", false, order++));

        // ===== School (স্কুল) =====
        order = 0;
        tiles.add(new TileEntity(AppConstants.CAT_SCHOOL, "শিক্ষক", "Teacher", "ic_teacher", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_SCHOOL, "বই", "Book", "ic_book", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_SCHOOL, "কলম", "Pen", "ic_pen", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_SCHOOL, "পড়া", "Study", "ic_study", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_SCHOOL, "খেলা", "Play", "ic_play", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_SCHOOL, "বন্ধু", "Friend", "ic_friend", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_SCHOOL, "ছুটি", "Holiday", "ic_holiday", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_SCHOOL, "ক্লাস", "Class", "ic_class", false, order++));

        // ===== Market (বাজার) =====
        order = 0;
        tiles.add(new TileEntity(AppConstants.CAT_MARKET, "কেনা", "Buy", "ic_buy", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_MARKET, "দাম", "Price", "ic_price", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_MARKET, "টাকা", "Money", "ic_money", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_MARKET, "সবজি", "Vegetables", "ic_vegetables", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_MARKET, "ফল", "Fruit", "ic_fruit_market", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_MARKET, "মিষ্টি", "Sweets", "ic_sweets", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_MARKET, "কাপড়", "Clothes", "ic_clothes_market", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_MARKET, "জুতা", "Shoes", "ic_shoes_market", false, order++));

        // ===== Mosque (মসজিদ) =====
        order = 0;
        tiles.add(new TileEntity(AppConstants.CAT_MOSQUE, "নামাজ", "Prayer", "ic_prayer", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_MOSQUE, "ওজু", "Ablution", "ic_ablution", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_MOSQUE, "আজান", "Call to Prayer", "ic_azan", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_MOSQUE, "কুরআন", "Quran", "ic_quran", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_MOSQUE, "জায়নামাজ", "Prayer Mat", "ic_prayer_mat", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_MOSQUE, "টুপি", "Cap", "ic_cap", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_MOSQUE, "জুম্মা", "Friday", "ic_friday", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_MOSQUE, "দোয়া", "Supplication", "ic_dua", false, order++));

        // ===== Hospital (হাসপাতাল) — body parts =====
        order = 0;
        tiles.add(new TileEntity(AppConstants.CAT_HOSPITAL, "মাথা", "Head", "ic_head", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_HOSPITAL, "চোখ", "Eye", "ic_eye", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_HOSPITAL, "কান", "Ear", "ic_ear", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_HOSPITAL, "নাক", "Nose", "ic_nose", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_HOSPITAL, "দাঁত", "Teeth", "ic_teeth", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_HOSPITAL, "গলা", "Throat", "ic_throat", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_HOSPITAL, "বুক", "Chest", "ic_chest", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_HOSPITAL, "পেট", "Stomach", "ic_stomach", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_HOSPITAL, "পিঠ", "Back", "ic_back", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_HOSPITAL, "হাত", "Hand", "ic_hand", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_HOSPITAL, "পা", "Foot", "ic_foot", false, order++));
        tiles.add(new TileEntity(AppConstants.CAT_HOSPITAL, "হাঁটু", "Knee", "ic_knee", false, order++));

        tileDao.insertAll(tiles);
    }
}
