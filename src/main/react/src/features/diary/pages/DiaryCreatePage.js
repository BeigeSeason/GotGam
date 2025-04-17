import React, { useState } from 'react';
import { Container, Typography, Box, Alert, Snackbar, Paper } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import DiaryForm from '../components/DiaryForm';
import axios from 'axios';

const DiaryCreatePage = () => {
  const navigate = useNavigate();
  const [error, setError] = useState(null);
  const [openSnackbar, setOpenSnackbar] = useState(false);

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

      // API 호출
      const response = await axios.post('/api/diaries', formattedData);
      
      // 성공 시 다이어리 상세 페이지로 이동
      setOpenSnackbar(true);
      setTimeout(() => {
        navigate(`/diaries/${response.data.id}`);
      }, 1500);
    } catch (err) {
      console.error('다이어리 생성 오류:', err);
      setError(
        err.response?.data?.message || 
        '다이어리 생성 중 오류가 발생했습니다. 다시 시도해주세요.'
      );
    }
  };

  const handleCloseSnackbar = () => {
    setOpenSnackbar(false);
  };

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Paper elevation={3} sx={{ p: 3, mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          새 다이어리 작성
        </Typography>
        <Typography variant="subtitle1" color="text.secondary" sx={{ mb: 4 }}>
          여행의 추억을 기록하고 공유해보세요.
        </Typography>
        
        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}
        
        <Box>
          <DiaryForm onSubmit={handleSubmit} />
        </Box>
      </Paper>
      
      <Snackbar
        open={openSnackbar}
        autoHideDuration={3000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert onClose={handleCloseSnackbar} severity="success">
          다이어리가 성공적으로 생성되었습니다!
        </Alert>
      </Snackbar>
    </Container>
  );
};

export default DiaryCreatePage;
