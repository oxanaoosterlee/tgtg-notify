package com.example.tgtgnotify;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class MainViewModel extends ViewModel {
    MainRepository mainRepository;



    public MainViewModel(MainRepository mainRepository){
        this.mainRepository = mainRepository;
    }


    public void refreshData(){ mainRepository.refreshData();
    }

    public LiveData<List<Item>> getItems(){
        return mainRepository.getItems();
    }

    public void logout(){this.mainRepository.logout();}
}
