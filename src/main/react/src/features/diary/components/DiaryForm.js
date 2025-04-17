import React, { useState, useEffect } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { TextField, Button, Checkbox, FormControlLabel, Grid, Typography, Paper, Box, Chip } from '@mui/material';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { styled } from '@mui/system';
import RegionSelector from './RegionSelector';
import TagInput from './TagInput';
import RichTextEditor from './RichTextEditor';

const StyledPaper = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(4),
  marginBottom: theme.spacing(2),
}));

const StyledDatePicker = styled(DatePicker)(({ theme }) => ({
  width: '100%',
  padding: '16.5px 14px',
  border: '1px solid rgba(0, 0, 0, 0.23)',
  borderRadius: '4px',
  fontSize: '1rem',
  '&:hover': {
    borderColor: 'rgba(0, 0, 0, 0.87)',
  },
  '&:focus': {
    borderColor: theme.palette.primary.main,
    borderWidth: '2px',
    outline: 'none',
  },
}));

const DiaryForm = ({ initialValues, onSubmit, isEdit = false }) => {
  const {
    register,
    handleSubmit,
    control,
    setValue,
    reset,
    formState: { errors },
  } = useForm({
    defaultValues: initialValues || {
      title: '',
      content: '',
      isPublic: true,
      startDate: new Date(),
      endDate: new Date(),
      region: '',
      areaCode: '',
      sigunguCode: '',
      totalCost: 0,
      tags: [],
    },
  });

  useEffect(() => {
    if (initialValues) {
      reset(initialValues);
    }
  }, [initialValues, reset]);

  const [tags, setTags] = useState(initialValues?.tags || []);

  const handleAddTag = (tag) => {
    if (tag && !tags.includes(tag)) {
      const newTags = [...tags, tag];
      setTags(newTags);
      setValue('tags', newTags);
    }
  };

  const handleDeleteTag = (tagToDelete) => {
    const newTags = tags.filter((tag) => tag !== tagToDelete);
    setTags(newTags);
    setValue('tags', newTags);
  };

  const handleRegionChange = (region, areaCode, sigunguCode) => {
    setValue('region', region);
    setValue('areaCode', areaCode);
    setValue('sigunguCode', sigunguCode);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <StyledPaper>
            <Typography variant="h6" gutterBottom>
              다이어리 기본 정보
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  {...register('title', { required: '제목을 입력해주세요.' })}
                  label="제목"
                  fullWidth
                  error={!!errors.title}
                  helperText={errors.title?.message}
                />
              </Grid>
              <Grid item xs={12}>
                <Controller
                  name="content"
                  control={control}
                  rules={{ required: '내용을 입력해주세요.' }}
                  render={({ field, fieldState }) => (
                    <RichTextEditor
                      value={field.value}
                      onChange={field.onChange}
                      error={!!fieldState.error}
                      helperText={fieldState.error?.message}
                    />
                  )}
                />
              </Grid>
            </Grid>
          </StyledPaper>
        </Grid>

        <Grid item xs={12}>
          <StyledPaper>
            <Typography variant="h6" gutterBottom>
              여행 정보
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <Typography variant="subtitle2" gutterBottom>
                  시작일
                </Typography>
                <Controller
                  name="startDate"
                  control={control}
                  rules={{ required: '시작일을 선택해주세요.' }}
                  render={({ field: { onChange, value } }) => (
                    <StyledDatePicker
                      selected={value ? new Date(value) : null}
                      onChange={(date) => onChange(date)}
                      dateFormat="yyyy-MM-dd"
                    />
                  )}
                />
                {errors.startDate && (
                  <Typography color="error" variant="caption">
                    {errors.startDate.message}
                  </Typography>
                )}
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography variant="subtitle2" gutterBottom>
                  종료일
                </Typography>
                <Controller
                  name="endDate"
                  control={control}
                  rules={{ required: '종료일을 선택해주세요.' }}
                  render={({ field: { onChange, value } }) => (
                    <StyledDatePicker
                      selected={value ? new Date(value) : null}
                      onChange={(date) => onChange(date)}
                      dateFormat="yyyy-MM-dd"
                      minDate={new Date(control._formValues.startDate)}
                    />
                  )}
                />
                {errors.endDate && (
                  <Typography color="error" variant="caption">
                    {errors.endDate.message}
                  </Typography>
                )}
              </Grid>
              <Grid item xs={12}>
                <Controller
                  name="region"
                  control={control}
                  render={({ field }) => (
                    <RegionSelector
                      initialRegion={field.value}
                      initialAreaCode={control._formValues.areaCode}
                      initialSigunguCode={control._formValues.sigunguCode}
                      onChange={handleRegionChange}
                    />
                  )}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  {...register('totalCost', {
                    required: '총 비용을 입력해주세요.',
                    min: { value: 0, message: '0 이상의 값을 입력해주세요.' },
                  })}
                  label="총 비용 (원)"
                  type="number"
                  fullWidth
                  error={!!errors.totalCost}
                  helperText={errors.totalCost?.message}
                />
              </Grid>
            </Grid>
          </StyledPaper>
        </Grid>

        <Grid item xs={12}>
          <StyledPaper>
            <Typography variant="h6" gutterBottom>
              태그 및 공개 설정
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TagInput onAddTag={handleAddTag} />
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5, mt: 1 }}>
                  {tags.map((tag) => (
                    <Chip
                      key={tag}
                      label={tag}
                      onDelete={() => handleDeleteTag(tag)}
                      color="primary"
                      variant="outlined"
                      size="small"
                    />
                  ))}
                </Box>
              </Grid>
              <Grid item xs={12}>
                <FormControlLabel
                  control={
                    <Controller
                      name="isPublic"
                      control={control}
                      render={({ field: { onChange, value } }) => (
                        <Checkbox checked={value} onChange={onChange} />
                      )}
                    />
                  }
                  label="공개 다이어리로 설정하기"
                />
              </Grid>
            </Grid>
          </StyledPaper>
        </Grid>

        <Grid item xs={12}>
          <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
            <Button variant="outlined" onClick={() => window.history.back()}>
              취소
            </Button>
            <Button type="submit" variant="contained" color="primary">
              {isEdit ? '수정하기' : '등록하기'}
            </Button>
          </Box>
        </Grid>
      </Grid>
    </form>
  );
};

export default DiaryForm;
