package com.example.dictionaryapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionaryapp.Activity.VideoLessonDetailActivity;
import com.example.dictionaryapp.Adapter.VideoLessonAdapter;
import com.example.dictionaryapp.Model.VideoLesson;
import com.example.dictionaryapp.R;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {
    
    private RecyclerView recyclerViewLessons;
    private VideoLessonAdapter adapter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        
        recyclerViewLessons = view.findViewById(R.id.recyclerViewLessons);
        recyclerViewLessons.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Tạo danh sách bài học
        List<VideoLesson> lessons = createLessonsList();
        
        // Tạo adapter và thiết lập cho RecyclerView
        adapter = new VideoLessonAdapter(getContext(), lessons, lesson -> {
            // Xử lý khi người dùng nhấn vào một bài học
            Intent intent = new Intent(getContext(), VideoLessonDetailActivity.class);
            intent.putExtra("lesson_title", lesson.getTitle());
            intent.putExtra("lesson_url", lesson.getVideoUrl());
            startActivity(intent);
        });
        
        recyclerViewLessons.setAdapter(adapter);
        
        return view;
    }
    
    // In the createLessonsList() method:
    
    private List<VideoLesson> createLessonsList() {
        List<VideoLesson> lessons = new ArrayList<>();
        
        // Using real YouTube URLs
        lessons.add(new VideoLesson(
                "3000 Từ vựng tiếng Anh Oxford thông dụng || Phát âm chuẩn || Part 1",
                "https://www.youtube.com/watch?v=dJmSxlZL-9M",
                android.R.drawable.ic_media_play));
                
        lessons.add(new VideoLesson(
                "3000 Từ vựng tiếng Anh Oxford thông dụng || Phát âm chuẩn || Part 2",
                "https://www.youtube.com/watch?v=TUg9qPoN2O8",
                android.R.drawable.ic_media_play));
                
        lessons.add(new VideoLesson(
                "100 bài giao tiếp tiếng Anh cơ bản || Learn Communication English || #1",
                "https://www.youtube.com/watch?v=8Qzw-Fhangw",
                android.R.drawable.ic_media_play));
                
        lessons.add(new VideoLesson(
                "BỘ 1000 Từ Vựng Tiếng Anh Giao Tiếp Trình Độ Căn Bản",
                "https://www.youtube.com/watch?v=TK3EI_lG0vw",
                android.R.drawable.ic_media_play));
                
        lessons.add(new VideoLesson(
                "60 TỪ VỰNG TIẾNG ANH CHỦ ĐỀ ĐỒ DÙNG CÁ NHÂN",
                "https://www.youtube.com/watch?v=Ot-HL4nzGIk",
                android.R.drawable.ic_media_play));
                
        lessons.add(new VideoLesson(
                "TỪ VỰNG TIẾNG ANH CHỦ ĐỀ DU LỊCH",
                "https://www.youtube.com/watch?v=LMb6-Cr9QeA",
                android.R.drawable.ic_media_play));
                
        lessons.add(new VideoLesson(
                "100 Từ Vựng Tiếng Anh Đồ Dùng Gia Đình Thông Dụng Nhất",
                "https://www.youtube.com/watch?v=oCimmcU6MRs",
                android.R.drawable.ic_media_play));
        
        return lessons;
    }
}