package com.foxridge.towerfox.repository;

import android.arch.lifecycle.LiveData;

import com.foxridge.towerfox.model.AppDatabase;
import com.foxridge.towerfox.model.Categories;

import java.util.List;

public class CategoriesRepository {
    private AppDatabase appDatabase;

    public CategoriesRepository(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }


    public LiveData<List<Categories>> getAllLiveCategory() {
        return appDatabase.getCategoriesDao().getAllCategoriesLiveData();
    }

    private void updateCategory(Categories categories) {
        appDatabase.getCategoriesDao().update(categories);
    }

    public void saveCategories(final Categories categories) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                appDatabase.getCategoriesDao().insert(categories);
            }
        });
        thread.start();
    }


    public Categories getCategory(String id) {
        Categories categories = appDatabase.getCategoriesDao().getCategory(id);
        if (categories != null) {
            return categories;
        } else {
            return null;
        }
    }

    public void deleteCategories(final Categories categories) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                appDatabase.getCategoriesDao().delete(categories);
            }
        });
        thread.start();
    }

    public void saveOrUpdateCategory(final Categories categories) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Categories  p = getCategory(categories.getCategoryID());
                if (p != null) {
                    appDatabase.getCategoriesDao().update(categories);
                } else {
                    appDatabase.getCategoriesDao().insert(categories);
                }
            }
        });
        thread.start();
    }

}
