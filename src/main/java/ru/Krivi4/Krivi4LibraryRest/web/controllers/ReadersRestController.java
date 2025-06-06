package ru.Krivi4.Krivi4LibraryRest.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.Krivi4.Krivi4LibraryRest.dtos.ReaderDTO;
import ru.Krivi4.Krivi4LibraryRest.mappers.dto.ReaderDTOMapper;
import ru.Krivi4.Krivi4LibraryRest.mappers.view.ReaderViewMapper;
import ru.Krivi4.Krivi4LibraryRest.models.Reader;
import ru.Krivi4.Krivi4LibraryRest.services.ReaderService;
import ru.Krivi4.Krivi4LibraryRest.views.ReaderResponse;


import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/readers")
@RequiredArgsConstructor
public class ReadersRestController {

    private final ReaderService readerService;
    private final ReaderDTOMapper readerDTOMapper;
    private final ReaderViewMapper readerViewMapper;


    /** Вывод всех читателей */
    @GetMapping()
    public List<ReaderResponse> getReaders(Pageable pageable){
        return readerService
                .findAll(pageable)
                .map(readerViewMapper::toResponse)
                .getContent();
    }

    /** Вывод читателя по id */
    @GetMapping("/{id}")
    public ReaderResponse getReader(@PathVariable("id") int id){
        return readerViewMapper.toResponse(readerService.findById(id));
    }



    /** Создание читателя + вывод сообщения */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody @Valid ReaderDTO readerDTO) {

        Reader saved = readerService.save(readerDTOMapper.toEntity(readerDTO));
        ReaderResponse resp = readerViewMapper.toResponse(saved);

        Map<String,Object> body = new LinkedHashMap<>();
        body.put("message",     "Пользователь с данными ниже добавлен");
        body.put("fullName",    resp.getFullName());
        body.put("dateOfBirth", resp.getDateOfBirth());
        body.put("email",       resp.getEmail());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(body);
    }



    /** Обновление читателя + вывод сообщения */
    @PutMapping("/{id}/edit")
    public ResponseEntity<Map<String, Object>> update(@PathVariable("id") int id, @RequestBody @Valid ReaderDTO readerDTO) {

        Reader updated = readerService.update(id, readerDTOMapper.toEntity(readerDTO));
        ReaderResponse resp = readerViewMapper.toResponse(updated);

        Map<String,Object> body = new LinkedHashMap<>();
        body.put("message",     "Пользователь с данными ниже обновлён");
        body.put("fullName",    resp.getFullName());
        body.put("dateOfBirth", resp.getDateOfBirth());
        body.put("email",       resp.getEmail());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(body);
    }



    /** Удаление читателя + вывод сообщения + если удалим читателя с взятой книгой
     *  - книга удалиться вслед за читателем из бд */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable("id") int id){

        Reader r = readerService.findById(id);
        ReaderResponse resp = readerViewMapper.toResponse(r);

        readerService.delete(id);
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("message",     "Пользователь с данными ниже удалён");
        body.put("fullName",    resp.getFullName());
        body.put("dateOfBirth", resp.getDateOfBirth());
        body.put("email",       resp.getEmail());

        return ResponseEntity
                .ok(body);
    }

}
