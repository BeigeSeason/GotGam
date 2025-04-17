import React, { useState, useEffect } from 'react';
import { Container, Typography, Box, Alert, Snackbar, Paper, CircularProgress } from '@mui/material';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import DiaryForm from '../../component/diary/DiaryForm';

const DiaryEditPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  
  const [diary, setDiary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [openSnackbar, setOpenSnackbar] = useState(false);

  // 기존 다이어리 정보 불러오기
  useEffect(() => {
    const fetchDiary = async () => {
      try {
        const response = await axios.get(`/api/diaries/${id}`);
        
        // 날짜 포맷 변환 (ISO 문자열을 Date 객체로)
        const diaryData = {
          ...response.data,
          startDate: new Date(response.data.startDate),
          endDate: new Date(response.data.endDate)
        };
        
        setDiary(diaryData);
        setError(null);
      } catch (err) {
        console.error('다이어리 정보 조회 오류:', err);
        setError(
          err.response?.data?.message || 
          '다이어리 정보를 불러오는 중 오류가 발생했습니다.'
        );
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      fetchDiary();
    }
  }, [id]);

  // 다이어리 수정 요청 처리
  const handleSubmit = async (data) => {
    try {
      // ISO 형식으로 날짜 변환
      const formattedData = {
        ...data,
        startDate: data.startDate instanceof Date 
          ? data.startDate.toISOString().split('T')[0] 
          : data.startDate,
        endDate: data.endDate instanceof Date 
          ? data.endDate.toISOString().split('T')[0] 
          : data.endDate
      };

      // API 호출 (PUT 요청)
      await axios.put(`/api/diaries/${id}`, formattedData);
      
      // 성공 메시지 표시 후 다이어리 상세 페이지로 이동
      setOpenSnackbar(true);
      setTimeout(() => {
        navigate(`/diary/${id}`);
      }, 1500);
    } catch (err) {
      console.error('다이어리 수정 오류:', err);
      setError(
        err.response?.data?.message || 
        '다이어리 수정 중 오류가 발생했습니다. 다시 시도해주세요.'
      );
    }
  };

  const handleCloseSnackbar = () => {
    setOpenSnackbar(false);
  };

  if (loading) {
    return (
      <Container maxWidth="md" sx={{ py: 8, textAlign: 'center' }}>
        <CircularProgress />
        <Typography variant="h6" sx={{ mt: 2 }}>
          다이어리 정보를 불러오는 중...
        </Typography>
      </Container>
    );
  }

  if (error && !diary) {
    return (
      <Container maxWidth="md" sx={{ py: 8 }}>
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
        <Box sx={{ textAlign: 'center', mt: 3 }}>
          <Typography variant="body1" sx={{ mb: 2 }}>
            다이어리 목록으로 돌아가거나, 다시 시도해주세요.
          </Typography>
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Paper elevation={3} sx={{ p: 3, mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          다이어리 수정
        </Typography>
        <Typography variant="subtitle1" color="text.secondary" sx={{ mb: 4 }}>
          여행 다이어리 내용을 수정하세요.
        </Typography>
        
        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}
        
        {diary && (
          <DiaryForm 
            initialValues={diary} 
            onSubmit={handleSubmit} 
            isEdit={true} 
          />
        )}
      </Paper>
      
      <Snackbar
        open={openSnackbar}
        autoHideDuration={3000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert onClose={handleCloseSnackbar} severity="success">
          다이어리가 성공적으로 수정되었습니다!
        </Alert>
      </Snackbar>
    </Container>
  );
};

export default DiaryEditPage;
